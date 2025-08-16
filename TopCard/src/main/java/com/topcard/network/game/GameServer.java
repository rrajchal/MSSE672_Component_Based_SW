package com.topcard.network.game;

import com.topcard.business.GameManager;
import com.topcard.business.PlayerManager;
import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles server-side communication and multiplayer game flow for TopCard.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 08/15/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class GameServer {

    private static final Logger logger = LogManager.getLogger(GameServer.class);

    private static final int PORT = Constants.GAME_PORT;
    private static final int START_GAME_TIMEOUT_SECONDS = 5;
    private static final int MAX_PLAYERS = Constants.MAX_PLAYERS;
    private static final int THREAD_POOL_SIZE = 4;
    private static final int FULL_LOBBY_CHECK_INTERVAL_MS = 100;
    private static final int TIMEOUT_CHECK_INTERVAL_MS = 500;
    private static final int BETS_ROUND_NUMBER = 1;

    private final List<ObjectOutputStream> clientOutputs = new CopyOnWriteArrayList<>();
    private final List<Player> connectedPlayers = new CopyOnWriteArrayList<>();
    private final List<Socket> clientSockets = new CopyOnWriteArrayList<>();

    private boolean gameStarted = false;
    private final ExecutorService clientThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    /**
     * Entry point for the server.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            new GameServer().start();
        } catch (Exception e) {
            logger.error("Error with Game Server", e);
        }
    }

    /**
     * Starts the server, listening for client connections.
     * @throws Exception if an error occurs while starting the server.
     */
    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        logger.info("TopCard Server started on port " + PORT);
        while (true) {
            try {
                if (connectedPlayers.size() < MAX_PLAYERS && !gameStarted) {
                    Socket socket = serverSocket.accept();
                    logger.info("New client connected from " + socket.getInetAddress());

                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    GameMessage joinMessage = (GameMessage) in.readObject();
                    if ("JOIN".equals(joinMessage.getType())) {
                        Player player = (Player) joinMessage.getPayload();

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
                    }
                } else {
                    Thread.sleep(FULL_LOBBY_CHECK_INTERVAL_MS);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error accepting client or reading join message: " + e.getMessage());
            }
        }
    }

    /**
     * Handles incoming game messages from clients.
     * @param message The received game message.
     * @param player The player who sent the message.
     */
    public synchronized void handleMessage(GameMessage message, Player player) {
        logger.debug("Server received message from " + player.getUsername() + ": " + message.getType());

        if ("START_GAME".equals(message.getType()) || "REMATCH".equals(message.getType())) {
            if (!gameStarted) {
                new Thread(() -> {
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
        GameManager manager = new GameManager(connectedPlayers);
        manager.startGame();
        manager.dealCards();

        List<Card[]> hands = manager.getHands();
        sendAll(new GameMessage("HANDS", hands));

        List<Player> updatedPlayers = manager.executeBettingRound(BETS_ROUND_NUMBER);
        sendAll(new GameMessage("POINTS_UPDATED", updatedPlayers));

        List<Player> winners = manager.determineWinner();
        sendAll(new GameMessage("WINNERS", winners));

        logger.info("Game round completed and updates sent to clients.");
    }

    /**
     * Broadcasts a message to all connected clients.
     * @param message The message to broadcast.
     */
    public synchronized void sendAll(GameMessage message) {
        for (ObjectOutputStream out : clientOutputs) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                logger.error("Failed to send message to client. " + e.getMessage());
            }
        }
    }

    /**
     * Fills the game with additional players if the lobby is not full.
     */
    private void fillMissingPlayersIfNeeded() {
        int missing = MAX_PLAYERS - connectedPlayers.size();
        if (missing <= 0) return;

        PlayerManager playerManager = new PlayerManager();
        List<Player> allPlayers = playerManager.getAllPlayers();
        allPlayers.removeIf(p -> connectedPlayers.stream()
                .anyMatch(cp -> cp.getUsername().equals(p.getUsername())));

        Collections.shuffle(allPlayers);

        for (int i = 0; i < missing; i++) {
            Player tempPlayer;
            if (i < allPlayers.size()) {
                tempPlayer = allPlayers.get(i);
            } else {
                String baseUsername = "Bot" + (i + 1);
                String tempUsername = baseUsername;
                int suffix = 1;
                while (playerManager.getPlayerByUsername(tempUsername) != null) {
                    tempUsername = baseUsername + "_" + suffix++;
                }
                LocalDate dob = LocalDate.of(1900, 1, 1);
                tempPlayer = new Player(tempUsername, "pass" + (i + 1), baseUsername, "Bot", dob);
                playerManager.addPlayer(tempPlayer);
                logger.warn("Created temporary player: " + tempPlayer.getUsername());
            }
            connectedPlayers.add(tempPlayer);
            logger.info("Added filler player: " + tempPlayer.getUsername());
        }
    }

    /**
     * Handles the disconnection of a client.
     * @param socket The client's socket.
     * @param in The client's input stream.
     * @param out The client's output stream.
     */
    public void removeClient(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        int index = clientSockets.indexOf(socket);
        if (index != -1) {
            logger.info("Removing disconnected player: " + connectedPlayers.get(index).getUsername());
            connectedPlayers.remove(index);
            clientOutputs.remove(index);
            clientSockets.remove(index);
        }
        try {
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