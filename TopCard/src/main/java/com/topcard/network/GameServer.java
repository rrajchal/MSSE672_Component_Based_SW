package com.topcard.network;

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
 */
public class GameServer {

    private static final Logger logger = LogManager.getLogger(GameServer.class);
    private static final int PORT = Constants.PORT;
    private static final int START_GAME_TIMEOUT_MS = 5;
    private static final int maxPlayer = Constants.MAX_PLAYERS;

    private final List<ObjectOutputStream> clientOutputs = new CopyOnWriteArrayList<>();
    private final List<Player> connectedPlayers = new CopyOnWriteArrayList<>();
    private final List<Socket> clientSockets = new CopyOnWriteArrayList<>();

    private boolean gameStarted = false;
    private final ExecutorService clientThreadPool = Executors.newFixedThreadPool(4);

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        logger.info("TopCard Server started on port " + PORT);

        while (true) {
            try {
                // Accept new clients if the game hasn't started and we have room
                if (connectedPlayers.size() < 4 && !gameStarted) {
                    Socket socket = serverSocket.accept();
                    logger.info("New client connected from " + socket.getInetAddress());

                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    // First message is always the JOIN message with the Player object
                    GameMessage joinMessage = (GameMessage) in.readObject();
                    if ("JOIN".equals(joinMessage.getType())) {
                        Player player = (Player) joinMessage.getPayload();

                        connectedPlayers.add(player);
                        clientOutputs.add(out);
                        clientSockets.add(socket);

                        logger.info("Player joined: " + player.getUsername() + ". Points: " + player.getPoints());
                        logger.info("Will start game in " + START_GAME_TIMEOUT_MS/1000 + " seconds if a player click on 'Player Game'. Players in Lobby: " +  connectedPlayers.size() + " of " + maxPlayer);
                        sendAll(new GameMessage("Game Lobby - Number of Players:", connectedPlayers.size()));

                        // Start a new thread to handle this client's input
                        clientThreadPool.submit(new GameServerHandler(this, socket, in, out));
                    }
                } else {
                    // Game is full or started, so stop accepting
                    Thread.sleep(100);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error accepting client or reading join message: " + e.getMessage());
            }
        }
    }

    /**
     * Handles game messages from clients.
     */
    public synchronized void handleMessage(GameMessage message, Player player) {
        logger.debug("Server received message from " + player.getUsername() + ": " + message.getType());

        if ("START_GAME".equals(message.getType()) || "REMATCH".equals(message.getType())) {
            // Only start the game if it hasn't already begun
            if (!gameStarted) {
                new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    logger.info("START_GAME received. Countdown to launch begins...");

                    while (System.currentTimeMillis() - startTime < START_GAME_TIMEOUT_MS) {
                        if (connectedPlayers.size() == 4) {
                            logger.info("Four players connected. Starting game immediately.");
                            break;
                        }
                        try {
                            Thread.sleep(500); // Check every half second
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    // Double check to prevent multiple starts
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

        List<Player> updatedPlayers = manager.executeBettingRound(1);
        sendAll(new GameMessage("POINTS_UPDATED", updatedPlayers));

        List<Player> winners = manager.determineWinner();
        sendAll(new GameMessage("WINNERS", winners));

        logger.info("Game round completed and updates sent to clients.");
    }

    /**
     * Broadcasts a GameMessage to all connected clients.
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

    private void fillMissingPlayersIfNeeded() {
        int missing = 4 - connectedPlayers.size();
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
     * Handles client disconnection.
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

    /**
     * Entry point for the server.
     */
    public static void main(String[] args) {
        try {
            new GameServer().start();
        } catch (Exception e) {
            logger.error("Error with Game Server");
        }
    }

    public List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }
}