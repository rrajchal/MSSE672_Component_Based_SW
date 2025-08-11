package com.topcard.network;

import com.topcard.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles communication for a single client connection on a separate thread.
 */
public class GameServerHandler implements Runnable {

    private static final Logger logger = LogManager.getLogger(GameServerHandler.class);

    private final GameServer server;
    private final Socket clientSocket;
    private final ObjectInputStream in;

    private final ObjectOutputStream out;
    private Player player;

    public GameServerHandler(GameServer server, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.server = server;
        this.clientSocket = socket;
        this.in = in;
        this.out = out;

        // Find the player object associated with this connection
        try {
            // The most recently added player corresponds to this socket.
            this.player = server.getConnectedPlayers().get(server.getConnectedPlayers().size() - 1);
        } catch (Exception e) {
            logger.error("Could not link handler to a player: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Blocks until a new message is received
                GameMessage message = (GameMessage) in.readObject();
                server.handleMessage(message, player);
            }
        } catch (IOException e) {
            logger.error("Client disconnected: " + (player != null ? player.getUsername() : "unknown"));
        } catch (ClassNotFoundException e) {
            logger.error("Invalid object received from client: " + e.getMessage());
        } finally {
            server.removeClient(clientSocket, in, out);
        }
    }
}