package com.topcard.network;

import com.topcard.business.GameManager;
import com.topcard.domain.Card;
import com.topcard.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Handles server-side communication and multiplayer game flow for TopCard.
 */
public class GameServer {

    private static final Logger logger = LogManager.getLogger(GameServer.class);
    private static final int PORT = 12345;

    private final List<ObjectOutputStream> clientOutputs = new ArrayList<>();
    private final List<ObjectInputStream> clientInputs = new ArrayList<>();
    private final List<Player> connectedPlayers = new ArrayList<>();

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        logger.info("TopCard Server started on port " + PORT);

        // Step 1: Accept 4 clients
        while (connectedPlayers.size() < 4) {
            Socket socket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            GameMessage joinMessage = (GameMessage) in.readObject();
            Player player = (Player) joinMessage.getPayload();

            connectedPlayers.add(player);
            clientOutputs.add(out);
            clientInputs.add(in);

            logger.info("Player joined: " + player.getUsername() + ". Points: " + player.getPoints());

            sendAll(new GameMessage("LOBBY_STATUS", connectedPlayers.size()));
        }

        logger.info("All players connected. Waiting for START_GAME...");

        // Step 2: Listen forever for START_GAME or REMATCH
        while (true) {
            for (ObjectInputStream in : clientInputs) {
                try {
                    GameMessage message = (GameMessage) in.readObject();
                    logger.debug("Server received: " + message.getType());

                    if ("START_GAME".equals(message.getType()) || "REMATCH".equals(message.getType())) {
                        logger.info("Starting game...");
                        beginGame();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    logger.error("⚠Error reading message: " + e.getMessage());
                }
            }

            Thread.sleep(100); // avoids busy loop
        }
    }

    /**
     * Runs a full game round for connected players.
     */
    private void beginGame() {
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
    private void sendAll(GameMessage message) {
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
     * Starts the server.
     */
    public static void main(String[] args) {
        try {
            new GameServer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
