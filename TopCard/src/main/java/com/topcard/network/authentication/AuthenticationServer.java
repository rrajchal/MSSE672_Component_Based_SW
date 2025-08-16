package com.topcard.network.authentication;

import com.topcard.domain.Player;
import com.topcard.business.PlayerManager;
import com.topcard.network.game.GameMessage;
import com.topcard.presentation.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multi-threaded authentication server for handling login and registration requests.
 */
public class AuthenticationServer {

    private static final Logger logger = LogManager.getLogger(AuthenticationServer.class);

    private static final int AUTH_PORT = Constants.AUTH_PORT; // a different port from GameServer
    private static final int THREAD_POOL_SIZE = 4;

    private final ExecutorService authThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final PlayerManager playerManager = new PlayerManager();

    public static void main(String[] args) {
        try {
            new AuthenticationServer().start();
        } catch (Exception e) {
            logger.error("Error starting Authentication Server", e);
        }
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(AUTH_PORT);
        logger.info("Authentication Server started on port " + AUTH_PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            logger.info("New authentication request from " + clientSocket.getInetAddress());
            authThreadPool.submit(new AuthHandler(clientSocket, playerManager));
        }
    }

    /**
     * Handles individual authentication requests in separate threads.
     */
    private static class AuthHandler implements Runnable {
        private final Socket socket;
        private final PlayerManager playerManager;

        public AuthHandler(Socket socket, PlayerManager playerManager) {
            this.socket = socket;
            this.playerManager = playerManager;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                GameMessage request = (GameMessage) in.readObject();
                String type = request.getType();

                if ("LOGIN".equals(type)) {
                    Player incoming = (Player) request.getPayload();
                    Player stored = playerManager.getPlayerByUsername(incoming.getUsername());

                    if (stored != null && stored.getPassword().equals(incoming.getPassword())) {
                        out.writeObject(new GameMessage("AUTH_SUCCESS", stored));
                        logger.info("Login successful for user: " + stored.getUsername());
                    } else {
                        out.writeObject(new GameMessage("AUTH_FAILURE", "Invalid username or password"));
                        logger.warn("Login failed for user: " + incoming.getUsername());
                    }

                } else if ("REGISTER".equals(type)) {
                    Player newPlayer = (Player) request.getPayload();
                    if (playerManager.getPlayerByUsername(newPlayer.getUsername()) == null) {
                        playerManager.addPlayer(newPlayer);
                        out.writeObject(new GameMessage("AUTH_SUCCESS", newPlayer));
                        logger.info("New user registered: " + newPlayer.getUsername());
                    } else {
                        out.writeObject(new GameMessage("AUTH_FAILURE", "Username already exists"));
                        logger.warn("Registration failed. Username already exists: " + newPlayer.getUsername());
                    }
                } else {
                    out.writeObject(new GameMessage("AUTH_FAILURE", "Unknown request type"));
                    logger.warn("Unknown authentication request type: " + type);
                }

                out.flush();

            } catch (IOException | ClassNotFoundException e) {
                logger.error("AuthHandler error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}
