package com.topcard.network;

import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.network.GameMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class SocketGameController {

    private static final Logger logger = LogManager.getLogger(SocketGameController.class);
    private ObjectOutputStream out;
    private ObjectInputStream in;
    Socket socket;

    private final Player localPlayer;

    public SocketGameController(Player player, String host) throws IOException {
        this.localPlayer = player;
        this.socket = new Socket(host, 12345);
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
                        case "HANDS" -> {
                            List<Card[]> hands = (List<Card[]>) msg.getPayload();
                            displayCards(hands);
                        }
                        case "POINTS_UPDATED" -> {
                            List<Player> updatedPlayers = (List<Player>) msg.getPayload();
                            displayPoints(updatedPlayers);
                        }
                        case "WINNERS" -> {
                            List<Player> winners = (List<Player>) msg.getPayload();
                            showWinners(winners);
                        }
                        default -> logger.error("Unrecognized message: " + msg.getType());
                    }
                }
            } catch (Exception e) {
                logger.info("Disconnected from server.");
            }
        }).start();
    }

    private void displayCards(List<Card[]> hands) {
        Card[] myHand = hands.stream()
                .filter(hand -> hand[0] != null && localPlayer.getHand()[0] != null)
                .findFirst().orElse(null);

        if (myHand != null) {
            logger.info("Your Hand:");
            for (Card c : myHand) {
                logger.info(" - " + c);
            }
        }
    }

    private void displayPoints(List<Player> players) {
        logger.info("Updated Points:");
        for (Player p : players) {
            logger.info(p.getUsername() + ": " + p.getPoints());
        }
    }

    private void showWinners(List<Player> winners) {
        StringBuilder sb = new StringBuilder("Winner(s): ");
        for (Player p : winners) {
            sb.append(p.getUsername()).append(" ");
        }
        JOptionPane.showMessageDialog(null, sb.toString().trim(), "Game Result", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Closes the client-side streams and socket.
     */
    public void disconnect() {
        try {
            if (out != null) {
                out.close();
                out = null; // Clear reference
            }
            if (in != null) {
                in.close();
                in = null; // Clear reference
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null; // Clear reference
            }
            logger.info("SocketGameController disconnected.");
        } catch (IOException e) {
            logger.error("Error during disconnect: " + e.getMessage());
        }
    }
}
