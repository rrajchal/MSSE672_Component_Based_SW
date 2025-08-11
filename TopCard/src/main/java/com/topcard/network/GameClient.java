package com.topcard.network;

import com.topcard.dao.player.PlayerDaoImpl;
import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.view.GameView;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

/**
 * Handles client-side communication with the TopCard server.
 * Manages game messages, server events, and GUI launching.
 */
public class GameClient {

    private static final Logger logger = LogManager.getLogger(GameClient.class);

    private static GameClient instance;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private GameClient() {}

    public static synchronized GameClient getInstance() {
        if (instance == null) {
            instance = new GameClient();
        }
        return instance;
    }

    /**
     * Connects to the server and registers the player.
     */
    public void connect(String host, Player player) throws IOException {
        Socket socket = new Socket(host, Constants.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        send(new GameMessage("JOIN", player));
        logger.debug("Connected to server as: " + player.getUsername());

        new Thread(this::listen).start();
    }

    /**
     * Sends a GameMessage to the server.
     */
    public synchronized void send(GameMessage msg) {
        if (out == null) {
            logger.error("Failed to send message: no server connection.");
            return;
        }
        try {
            out.writeObject(msg);
            out.flush();
            logger.info("Sent message: " + msg.getType());
        } catch (IOException e) {
            logger.error("Error sending message: " + e.getMessage());
        }
    }

    /**
     * Listens for incoming messages from the server.
     */
    @SuppressWarnings("unchecked")
    private void listen() {
        try {
            while (true) {
                GameMessage msg = (GameMessage) in.readObject();
                logger.debug("Received message: " + msg.getType());

                switch (msg.getType()) {
                    case "HANDS" -> displayHands((List<Card[]>) msg.getPayload());
                    case "WINNERS" -> announceWinners((List<Player>) msg.getPayload());
                    case "POINTS_UPDATED" -> launchGameView((List<Player>) msg.getPayload());
                    case "SHUTDOWN" -> {
                        logger.info("Received SHUTDOWN message from server. Terminating listener.");
                        throw new EOFException("Server requested shutdown"); // This will break the while(true) loop
                    }
                    default -> logger.debug("Unknown message type: " + msg.getType());
                }
            }
        } catch (Exception e) {
            logger.error("🔌 Disconnected from server. " + e.getMessage());
        }
    }

    private void displayHands(List<Card[]> hands) {
//        logger.info("Your Hand:");
//        hands.forEach(hand -> {
//            for (Card c : hand) {
//                logger.info(" - " + c);
//            }
//        });
    }

    private void announceWinners(List<Player> winners) {
//        logger.info("Winners:");
//        winners.forEach(w -> logger.info(" - " + w.getUsername()));
    }

    private void launchGameView(List<Player> players) {
        new JFXPanel(); // Ensures JavaFX thread is initialized
        Platform.setImplicitExit(false);

        Platform.runLater(() -> {
            logger.info("Launching GameView...");
            try {
                GameView view = new GameView(players);
                Stage stage = new Stage();
                view.start(stage);
            } catch (Exception ex) {
                logger.error("Failed to launch GameView: " + ex.getMessage());
            }
        });
    }

    /**
     * Closes client-side input and output streams to gracefully terminate the connection with the server.
     * Helps prevent resource leaks and ensures the server recognizes the end of communication.
     */
    public void disconnect() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            logger.error("Error during disconnect: {}", e.getMessage());
        }
    }

    /**
     * For command-line testing.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("No username provided.");
            return;
        }

        String username = args[0];
        logger.debug("Loading player: " + username);
        PlayerDaoImpl dao = new PlayerDaoImpl();
        Optional<Player> player = dao.getPlayerByUsername(username);

        if (player.isEmpty()) {
            logger.error("Player not found: " + username);
            return;
        }

        new Thread(() -> {
            try {
                GameClient.getInstance().connect("localhost", player.get());
            } catch (IOException e) {
                logger.error("Connection error: " + e.getMessage());
            }
        }).start();
    }
}
