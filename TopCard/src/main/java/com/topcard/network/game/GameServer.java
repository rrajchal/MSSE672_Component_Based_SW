package com.topcard.network.game;

import com.topcard.business.GameManager;
import com.topcard.business.PlayerManager;
import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import com.topcard.service.game.GameService;
import com.topcard.service.game.IGameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Handles server-side communication and multiplayer game flow for TopCard.
 */
@Component
public class GameServer {

    private static final Logger logger = LogManager.getLogger(GameServer.class);

    private static final int PORT = Constants.GAME_PORT;
    private static final int START_GAME_TIMEOUT_SECONDS = 5;
    private static final int MAX_PLAYERS = Constants.MAX_PLAYERS;
    private static final int THREAD_POOL_SIZE = 4;
    private static final int FULL_LOBBY_CHECK_INTERVAL_MS = 5000;
    private static final int TIMEOUT_CHECK_INTERVAL_MS = 500;
    private static final int BETS_ROUND_NUMBER = 1;
    private static final int CLIENT_SOCKET_READ_TIMEOUT_MS = 5000;

    private final List<ObjectOutputStream> clientOutputs = new CopyOnWriteArrayList<>();
    private final List<Player> connectedPlayers = new CopyOnWriteArrayList<>();
    private final List<Socket> clientSockets = new CopyOnWriteArrayList<>();

    private volatile boolean gameStarted = false;
    private volatile boolean running = false; // Controls the server's main accept loop for graceful shutdown

    private final ExecutorService clientThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    ServerSocket serverSocket;

    @Autowired
    PlayerManager playerManager;

    @Autowired
    private ApplicationContext context;

    /**
     * Main entry point for the server.
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.topcard");
        GameServer server = applicationContext.getBean(GameServer.class);
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Error with Game Server", e);
        }
    }

    /**
     * Starts the server, listening for client connections.
     * @throws Exception if an error occurs while starting the server.
     */
    public void start() throws Exception {
        serverSocket = new ServerSocket(PORT);
        running = true;
        logger.info("TopCard Server started on port " + PORT);

        while (running) { // Loop continues as long as server is running
            Socket socket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;

            try {
                if (connectedPlayers.size() < MAX_PLAYERS && !gameStarted) {
                    socket = serverSocket.accept();
                    if (!running) { // Check flag immediately after accept in case stop() was called
                        logger.info("Server stopping, closing newly accepted socket from " + socket.getInetAddress());
                        socket.close();
                        break;
                    }

                    logger.info("New client connected from " + socket.getInetAddress());
                    socket.setSoTimeout(CLIENT_SOCKET_READ_TIMEOUT_MS);

                    try {
                        out = new ObjectOutputStream(socket.getOutputStream());
                        in = new ObjectInputStream(socket.getInputStream());
                    } catch (EOFException | SocketException e) {
                        // Handles premature client disconnects or specific socket issues during stream setup
                        String logMsg = "Stream setup exception for " + socket.getInetAddress() + ": " + e.getMessage();
                        if (!running) logger.info("Cleanly handled " + logMsg + " (during shutdown)");
                        else logger.debug("Transient " + logMsg + " (client disconnected early)");
                        if (socket != null && !socket.isClosed()){
                            try {
                                socket.close();
                            } catch (IOException ignored) {}
                        }
                        continue;
                    }

                    GameMessage joinMessage = (GameMessage) in.readObject();
                    if ("JOIN".equals(joinMessage.getType())) {
                        Player player = (Player) joinMessage.getPayload();

                        if (player == null || player.getUsername() == null || player.getUsername().trim().isEmpty()) {
                            logger.warn("Client from " + socket.getInetAddress() + " sent invalid JOIN payload. Connection rejected.");
                            in.close(); out.close(); socket.close();
                            continue;
                        }

                        if (!connectedPlayers.contains(player)) {
                            connectedPlayers.add(player);
                            clientOutputs.add(out);
                            clientSockets.add(socket);

                            logger.info("Player joined: " + player.getUsername() + ". Players in Lobby: " +  connectedPlayers.size() + " of " + MAX_PLAYERS);
                            sendAll(new GameMessage("Game Lobby - Number of Players:", connectedPlayers.size()));
                            clientThreadPool.submit(new GameServerHandler(this, socket, in, out));
                        } else {
                            logger.warn("Player " + player.getUsername() + " attempted to join twice. Connection rejected.");
                            in.close(); out.close(); socket.close();
                        }
                    } else {
                        logger.warn("Received unexpected message type: " + joinMessage.getType() + " during join phase from " + socket.getInetAddress() + ". Connection rejected by immediate close.");
                        in.close(); out.close(); socket.close();
                    }
                } else {
                    Thread.sleep(FULL_LOBBY_CHECK_INTERVAL_MS);
                }
            } catch (SocketTimeoutException e) { // Catches timeouts on initial read
                logger.warn("Client connection timed out during initial JOIN message from " + (socket != null ? socket.getInetAddress() : "unknown") + ": " + e.getMessage());
                if (socket != null && !socket.isClosed()) try { socket.close(); } catch (IOException ignored) {}
            } catch (SocketException se) { // Catches socket-specific errors, including Socket closed
                if (!running && (se.getMessage() != null && se.getMessage().toLowerCase().contains("socket closed"))) {
                    logger.info("Game Server socket closed, exiting accept loop cleanly.");
                    break;
                } else {
                    logger.error("Socket error in accept loop: " + se.getMessage(), se);
                }
            } catch (IOException | ClassNotFoundException e) { // Catches other I/O errors and deserialization issues
                logger.error("Error accepting client or reading join message: " + e.getMessage(), e);
                if (socket != null && !socket.isClosed()) try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Stops the game server gracefully.
     * @throws IOException if an I/O error occurs while closing the socket.
     * @throws InterruptedException if the current thread is interrupted while waiting for client threads to terminate.
     */
    public void stop() throws IOException, InterruptedException {
        running = false; // Signal main loop to terminate
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close(); // Interrupts blocking accept() call
            logger.info("Game Server socket closed.");
        }
        for (Socket clientSocket : clientSockets) { // Close all connected client sockets
            try { if (!clientSocket.isClosed()) clientSocket.close(); } catch (IOException ignored) {}
        }
        clientOutputs.clear(); connectedPlayers.clear(); clientSockets.clear();

        clientThreadPool.shutdown(); // Shutdown client handling threads
        try {
            if (!clientThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                clientThreadPool.shutdownNow(); // Force shutdown if not terminated
                logger.warn("Game client thread pool did not terminate cleanly.");
            }
        } catch (InterruptedException e) {
            clientThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
            logger.error("Game client thread pool termination interrupted.", e);
        }
    }

    /**
     * Handles incoming game messages from clients.
     */
    public synchronized void handleMessage(GameMessage message, Player player) {
        logger.debug("Server received message from " + player.getUsername() + ": " + message.getType());

        if ("START_GAME".equals(message.getType()) || "REMATCH".equals(message.getType())) {
            if (!gameStarted) {
                new Thread(() -> { // Starts a new thread for game launch countdown
                    long startTime = System.currentTimeMillis();
                    logger.info("START_GAME received. Countdown to launch begins...");

                    while (System.currentTimeMillis() - startTime < START_GAME_TIMEOUT_SECONDS * 1000) {
                        if (connectedPlayers.size() == MAX_PLAYERS) {
                            logger.info("Four players connected. Starting game immediately.");
                            break;
                        }
                        try {
                            Thread.sleep(TIMEOUT_CHECK_INTERVAL_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    if (!gameStarted) {
                        logger.info("Countdown complete. Starting game now.");
                        fillMissingPlayersIfNeeded();
                        beginGame();
                        gameStarted = true;
                    }
                }).start();
            }
        }
    }

    /**
     * Runs a full game round for connected players.
     */
    private synchronized void beginGame() {
        IGameService newGameService = context.getBean(IGameService.class);
        newGameService.setPlayers(connectedPlayers);
        GameManager gameManager = new GameManager(newGameService);

        gameManager.startGame();
        gameManager.dealCards();

        List<Card[]> hands = gameManager.getHands();
        sendAll(new GameMessage("HANDS", hands));

        List<Player> updatedPlayers = gameManager.executeBettingRound(BETS_ROUND_NUMBER);
        sendAll(new GameMessage("POINTS_UPDATED", updatedPlayers));

        List<Player> winners = gameManager.determineWinner();
        sendAll(new GameMessage("WINNERS", winners));

        logger.info("Game round completed and updates sent to clients.");
    }

    /**
     * Broadcasts a message to all connected clients.
     */
    public synchronized void sendAll(GameMessage message) {
        List<ObjectOutputStream> outputsToSend = new ArrayList<>(clientOutputs);
        for (ObjectOutputStream out : outputsToSend) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                logger.error("Failed to send message to client: " + e.getMessage());
            }
        }
    }

    /**
     * Fills the game with additional bot players if the lobby is not full.
     */
    private void fillMissingPlayersIfNeeded() {
        int missing = MAX_PLAYERS - connectedPlayers.size();
        if (missing <= 0) return;

        List<Player> allPlayers = playerManager.getAllPlayers();
        // Remove already connected players from potential bots list
        allPlayers.removeIf(p -> connectedPlayers.stream().anyMatch(cp -> cp.getUsername().equals(p.getUsername())));

        Collections.shuffle(allPlayers); // Randomize bot selection

        for (int i = 0; i < missing; i++) {
            Player tempPlayer;
            if (i < allPlayers.size()) {
                tempPlayer = allPlayers.get(i);
            } else {
                // Create new bot if no existing player is available
                String baseUsername = "Bot" + (i + 1);
                String tempUsername = baseUsername;
                int suffix = 1;
                while (playerManager.getPlayerByUsername(tempUsername) != null) tempUsername = baseUsername + "_" + suffix++;
                tempPlayer = new Player(tempUsername, "pass" + (i + 1), baseUsername, "Bot", LocalDate.of(1900, 1, 1));
                playerManager.addPlayer(tempPlayer);
                logger.warn("Created temporary player: " + tempPlayer.getUsername());
            }
            connectedPlayers.add(tempPlayer);
            logger.info("Added filler player: " + tempPlayer.getUsername());
        }
    }

    /**
     * Handles the disconnection of a client.
     */
    public void removeClient(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        int index = clientSockets.indexOf(socket);
        if (index != -1) {
            logger.info("Removing disconnected player: " + connectedPlayers.get(index).getUsername());
            connectedPlayers.remove(index);
            clientOutputs.remove(index);
            clientSockets.remove(index);
        }
        try { // Close client resources
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.error("Error closing client streams: " + e.getMessage());
        }
    }

    public List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }
}