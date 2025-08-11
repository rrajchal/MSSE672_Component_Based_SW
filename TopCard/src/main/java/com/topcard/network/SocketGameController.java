package com.topcard.network;

import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Handles client-side communication with the TopCard game server over a socket connection.
 * This controller manages the local player's connection, sends a join request,
 * listens for game updates (hands, points, winners), and displays relevant information.
 * It runs a background thread to process incoming messages and provides a method to disconnect cleanly.
 */
public class SocketGameController {

    private static final Logger logger = LogManager.getLogger(SocketGameController.class);
    private ObjectOutputStream out;
    private ObjectInputStream in;
    Socket socket;

    private final Player localPlayer;

    public SocketGameController(Player player, String host) throws IOException {
        this.localPlayer = player;
        this.socket = new Socket(host, Constants.PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        sendJoinRequest();
        listen();
    }

    private void sendJoinRequest() throws IOException {
        GameMessage join = new GameMessage("JOIN", localPlayer);
        out.writeObject(join);
        out.flush();
    }

    private void listen() {
        new Thread(() -> {
            try {
                while (true) {
                    GameMessage msg = (GameMessage) in.readObject();
                    switch (msg.getType()) {
                        case "HANDS", "POINTS_UPDATED", "WINNERS" -> {
                            // These message types are processed by the game logic.
                            // The controller acknowledges them to maintain compatibility but does not act on them directly.
                            // No action or print messages needed here.
                        }
                        default -> logger.error("Unrecognized message: " + msg.getType());
                    }
                }
            } catch (Exception e) {
                logger.info("Disconnected from server.");
            }
        }).start();
    }

    /**
     * Closes the client-side streams and socket and clear references.
     */
    public void disconnect() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            logger.info("SocketGameController disconnected.");
        } catch (IOException e) {
            logger.error("Error during disconnect: " + e.getMessage());
        }
    }
}
