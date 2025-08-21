package com.topcard.network.authentication;

import com.topcard.domain.Player;
import com.topcard.business.PlayerManager;
import com.topcard.network.game.GameMessage;
import com.topcard.presentation.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Multithreaded authentication server for handling login requests.
 */
public class AuthenticationServer {

    private static final Logger logger = LogManager.getLogger(AuthenticationServer.class);

    private int authPort;
    private static final int THREAD_POOL_SIZE = 4;
    private static final int CLIENT_SOCKET_READ_TIMEOUT_MS = 5000; // 5-second timeout for client reads

    private final ExecutorService authThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final PlayerManager playerManager;
    private ServerSocket serverSocket;

    // For error handling and tracking failed login attempts
    private static final ConcurrentHashMap<String, Integer> failedLoginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCKOUT_DURATION_MS = 60 * 1000; // 60 seconds lockout
    private static final ConcurrentHashMap<String, Long> lockedOutUsers = new ConcurrentHashMap<>();

    public AuthenticationServer() {
        this.authPort = Constants.AUTH_PORT;
        // Initialize Spring context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.topcard");
        context.refresh();

        // Retrieve PlayerManager from Spring
        this.playerManager = context.getBean(PlayerManager.class);
    }

    /**
     * Main entry point for starting AuthenticationServer.
     */
    public static void main(String[] args) {
        try {
            new AuthenticationServer().start();
        } catch (Exception e) {
            logger.error("Error starting Authentication Server", e);
        }
    }

    /**
     * Starts the authentication server, binding it to the specified port.
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(authPort);
        logger.info("Authentication Server started on port " + authPort);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("New authentication request from " + clientSocket.getInetAddress());
                authThreadPool.submit(new AuthHandler(clientSocket, playerManager));
            } catch (SocketException se) {
                if (se.getMessage().contains("Socket closed")) {
                    logger.info("Authentication Server socket closed, exiting accept loop.");
                    break;
                } else {
                    logger.error("Server accept loop socket error: " + se.getMessage(), se);
                }
            } catch (IOException e) {
                logger.error("Server accept loop I/O error: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Stops the AuthenticationServer gracefully.
     */
    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            logger.info("Authentication Server socket closed.");
        }
        authThreadPool.shutdown(); // Stop accepting new tasks
        try {
            if (!authThreadPool.awaitTermination(2, TimeUnit.SECONDS)) { // for graceful shutdown
                authThreadPool.shutdownNow();
                logger.warn("Authentication Thread Pool did not terminate cleanly.");
            }
        } catch (InterruptedException e) {
            authThreadPool.shutdownNow();
            Thread.currentThread().interrupt(); // Restore interrupt status
            logger.error("Authentication Thread Pool termination interrupted.", e);
        }
    }

    /**
     * Handles individual authentication requests.
     */
    public static class AuthHandler implements Runnable {
        private final Socket socket;
        private final PlayerManager playerManager;

        public AuthHandler(Socket socket, PlayerManager playerManager) {
            this.socket = socket;
            this.playerManager = playerManager;
        }

        @Override
        public void run() {
            InetAddress clientAddress = socket.getInetAddress();
            String clientIp = clientAddress.getHostAddress();

            try {
                socket.setSoTimeout(CLIENT_SOCKET_READ_TIMEOUT_MS); // Set read timeout for this client socket

                try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    if (isClientLockedOut(clientIp)) {
                        out.writeObject(new GameMessage("AUTH_FAILURE", "Too many failed attempts. Please try again later."));
                        logger.warn("Request from locked out IP: " + clientIp);
                        out.flush();
                        return;
                    }

                    GameMessage request = (GameMessage) in.readObject();
                    String type = request.getType();

                    if ("LOGIN".equals(type)) {
                        Player incoming = (Player) request.getPayload();
                        handleLoginRequest(incoming, out, clientIp);
                        // not tested yet (For future upgrade)
//                    } else if ("REGISTER".equals(type)) {
//                        Player newPlayer = (Player) request.getPayload();
//                        handleRegisterRequest(newPlayer, out, clientIp);
                    } else {
                        out.writeObject(new GameMessage("AUTH_FAILURE", "Unknown request type"));
                        logger.warn("Unknown authentication request type: " + type + " from " + clientIp);
                    }

                    out.flush();
                }
            } catch (SocketTimeoutException e) {
                logger.warn("AuthHandler: Read timed out from client " + clientIp + ". " + e.getMessage());
            } catch (IOException | ClassNotFoundException e) {
                logger.error("AuthHandler error for client " + clientIp + ": " + e.getMessage(), e);
            } finally {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    logger.error("Error closing socket for client " + clientIp + ": " + e.getMessage(), e);
                }
            }
        }

        /**
         * Handles LOGIN requests with input validation and failed attempt tracking.
         */
        private void handleLoginRequest(Player incoming, ObjectOutputStream out, String clientIp) throws IOException {
            // Input Validation: Check for null or empty credentials
            if (incoming == null || incoming.getUsername() == null || incoming.getUsername().trim().isEmpty() ||
                    incoming.getPassword() == null || incoming.getPassword().isEmpty()) {
                out.writeObject(new GameMessage("AUTH_FAILURE", "Invalid login credentials. Username and password cannot be empty."));
                logger.warn("Login failed: Invalid input received from " + clientIp);
                incrementFailedAttempts(clientIp); // Count invalid input as a failed attempt
                return;
            }

            Player stored = playerManager.getPlayerByUsername(incoming.getUsername());

            if (stored != null && stored.getPassword().equals(incoming.getPassword())) {
                out.writeObject(new GameMessage("AUTH_SUCCESS", stored));
                logger.info("Login successful for user: " + stored.getUsername() + " from " + clientIp);
                failedLoginAttempts.remove(clientIp); // Reset failed attempts on success
                lockedOutUsers.remove(clientIp); // Clear lockout status
            } else {
                out.writeObject(new GameMessage("AUTH_FAILURE", "Invalid username or password"));
                logger.warn("Login failed for user: " + incoming.getUsername() + " from " + clientIp);
                incrementFailedAttempts(clientIp); // Track failed attempt
            }
        }

        // For future upgrades
//        /**
//         * Handles REGISTER requests with input validation and uniqueness check.
//         */
//        private void handleRegisterRequest(Player newPlayer, ObjectOutputStream out, String clientIp) throws IOException {
//            // Input Validation: Check for null or empty credentials for registration
//            if (newPlayer == null || newPlayer.getUsername() == null || newPlayer.getUsername().trim().isEmpty() ||
//                    newPlayer.getPassword() == null || newPlayer.getPassword().isEmpty()) {
//                out.writeObject(new GameMessage("AUTH_FAILURE", "Registration failed: Username and password cannot be empty."));
//                logger.warn("Registration failed: Invalid input received from " + clientIp);
//                return;
//            }
//
//            if (playerManager.getPlayerByUsername(newPlayer.getUsername()) == null) {
//                playerManager.addPlayer(newPlayer);
//                out.writeObject(new GameMessage("AUTH_SUCCESS", newPlayer));
//                logger.info("New user registered: " + newPlayer.getUsername() + " from " + clientIp);
//            } else {
//                out.writeObject(new GameMessage("AUTH_FAILURE", "Username already exists"));
//                logger.warn("Registration failed. Username already exists: " + newPlayer.getUsername() + " from " + clientIp);
//            }
//        }

        /**
         * Increments failed login attempts for an IP and locks out if max attempts reached.
         */
        private void incrementFailedAttempts(String ip) {
            failedLoginAttempts.compute(ip, (key, count) -> (count == null) ? 1 : count + 1);
            if (failedLoginAttempts.get(ip) >= MAX_FAILED_ATTEMPTS) {
                lockedOutUsers.put(ip, System.currentTimeMillis() + LOCKOUT_DURATION_MS);
                logger.warn("IP {} locked out due to too many failed login attempts.", ip);
            }
        }

        /**
         * Checks if a client IP is currently locked out.
         */
        private boolean isClientLockedOut(String ip) {
            Long lockoutEndTime = lockedOutUsers.get(ip);
            if (lockoutEndTime != null) {
                if (System.currentTimeMillis() < lockoutEndTime) {
                    return true; // Still locked out
                } else {
                    lockedOutUsers.remove(ip); // Lockout period expired, clear status
                    failedLoginAttempts.remove(ip);
                }
            }
            return false;
        }
    }

    public void setAuthPort(int authPort) {
        this.authPort = authPort;
    }
}