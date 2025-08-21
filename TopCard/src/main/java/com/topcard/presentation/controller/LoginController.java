package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.network.game.GameClient;
import com.topcard.network.game.GameMessage;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.InternalFrame;
import com.topcard.presentation.common.Validation;
import com.topcard.presentation.view.LoginView;
import com.topcard.presentation.view.OptionsView;
import com.topcard.presentation.view.SignUpView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import javax.swing.*;

/**
 * This class represents the controller for the login view.
 * It manages the user interactions for logging in.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 08/15/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
public class LoginController extends JFrame {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private LoginView loginView;
    private JDesktopPane desktopPane;
    private PlayerManager playerManager;  // Injected by Spring

    @Autowired
    private ApplicationContext context;

//    /**
//     * Constructor to initialize the login controller with the given login view.
//     *
//     * @param loginView the login view
//     */
//    public LoginController(LoginView loginView, JDesktopPane desktopPane, PlayerManager playerManager) {
//        this.loginView = loginView;
//        this.desktopPane = desktopPane;
//        this.playerManager = playerManager;
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        initController();
//    }

    /**
     * Initializes the controller by setting up the action listeners.
     */
    private void initController() {
        logger.info("Initializing Login Controller");
        // for login button
        loginView.getLoginButton().addActionListener(e -> handleLogin());

        // for signup link
        loginView.getSignUpLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSignUp();
            }
        });
    }

    /**
     * Handles the login process when the login button is clicked.
     * It gathers the user inputs, authenticates, and determines whether
     * to launch the game in online or offline mode.
     */
    private void handleLogin() {
        String username = loginView.getUsernameField().getText();
        String password = new String(loginView.getPasswordField().getPassword());

        if (validateInputs(loginView.getUsernameField(), username, Constants.USERNAME_CANNOT_HAVE_SPACES) &&
                validateInputs(loginView.getPasswordField(), password, Constants.PASSWORD_CANNOT_HAVE_SPACES) &&
                authenticate(username, password)) {

            Player loggedInPlayer = playerManager.getPlayerByUsername(username);

            // Attempt to connect to the server first
            if (areServersRunning()) {
                GameMessage response = sendAuthRequest("LOGIN", loggedInPlayer);
                Player authenticatedPlayer = null;
                if (response != null && "AUTH_SUCCESS".equals(response.getType())) {
                    authenticatedPlayer = (Player) response.getPayload();
                }
                logger.info("Server is available. Attempting to join online game.");
                try {
                    if (authenticatedPlayer != null) {
                        GameClient.getInstance().connect(Constants.LOCAL_HOST, authenticatedPlayer);
                        launchOptionsView(username, true); // Pass a flag indicating online mode
                    } else {
                        throw new Exception("No authenticated player");
                    }
                } catch (Exception e) {
                    logger.error("Failed to join multiplayer server: " + e.getMessage());
                    launchOptionsView(username, false); // Fallback to offline
                }
            } else {
                logger.info("Multiplayer server is not running. Launching in offline mode.");
                launchOptionsView(username, false); // Launch in offline mode
            }
        } else {
            loginView.getMessageLabel().setForeground(Color.RED);
            loginView.getMessageLabel().setText(Constants.INVALID_USERNAME_OR_PASSWORD);
        }
    }

    private GameMessage sendAuthRequest(String type, Player player) {
        try (Socket socket = new Socket(Constants.LOCAL_HOST, Constants.AUTH_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(new GameMessage(type, player));
            out.flush();
            return (GameMessage) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Authentication request failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to launch the OptionsView and manage the transition.
     * @param username The logged-in player's username.
     * @param isOnline A flag indicating if the game is in online mode.
     */
    private void launchOptionsView(String username, boolean isOnline) {
        JInternalFrame loginFrame = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, loginView.getLoginPanel());
        if (loginFrame != null) {
            loginFrame.dispose();
        }

        OptionsView optionsView = context.getBean(OptionsView.class);
        OptionsController optionsController = context.getBean(OptionsController.class);
        optionsController.initialize(optionsView, username, isOnline, desktopPane);

        InternalFrame.addInternalFrame(desktopPane, "Choose an Option", optionsView.getOptionsPanel(), 400, 200, false);
    }

    /**
     * Checks if the servers are reachable by attempting a socket connection.
     * This method acts as a client-side replacement for a server-side method.
     *
     * @return true if a connection can be established, false otherwise.
     */
    private boolean areServersRunning() {
        boolean authAvailable = isServerReachable(Constants.LOCAL_HOST, Constants.AUTH_PORT, "Authentication");
        boolean gameAvailable = isServerReachable(Constants.LOCAL_HOST, Constants.GAME_PORT, "Game");

        return authAvailable && gameAvailable;
    }

    /**
     * Attempts to establish a TCP connection to the specified server and port to verify whether a remote server is actively
     * listening and reachable from the client side. It performs a non-blocking connection attempt with a timeout and logs the result.
     * This is useful for determining server availability before initiating gameplay or authentication logic.
     *
     * @param host the hostname or IP address of the server (e.g., "localhost")
     * @param port the port number the server is expected to be listening on
     * @param serverName a descriptive name for logging purposes (e.g., "Authentication", "Game")
     * @return true if the server is reachable within the timeout window; false otherwise
     */
    private boolean isServerReachable(String host, int port, String serverName) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            logger.info(serverName + " Server is reachable on port " + port);
            return true;
        } catch (SocketTimeoutException e) {
            logger.warn(serverName + " Server connection timed out.");
            return false;
        } catch (IOException e) {
            logger.warn(serverName + " Server is not available at " + host + ":" + port + " (" + e.getMessage() + ")");
            return false;
        }
    }

    /**
     * Validates the user inputs for username and password.
     *
     * @param textField the text field to validate
     * @param text the text entered by the user
     * @param errorMessage the error message to display if invalid
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs(JTextField textField, String text, String errorMessage) {
        return Validation.validateForSpaces(textField, text, errorMessage);
    }

    /**
     * Authenticates the user by verifying the provided username and password.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return true if the username and password are valid, false otherwise
     */
    private boolean authenticate(String username, String password) {
        Player player = playerManager.getPlayerByUsername(username);
        return username != null && !username.isEmpty() &&
                password != null && !password.isEmpty() &&
                player != null && player.getUsername().equals(username) &&
                playerManager.verifyPassword(password, player.getPassword());
    }

    /**
     * Handles the sign-up process when the sign-up link is clicked.
     * It opens the SignUpView and disables the Login frame.
     */
    private void handleSignUp() {
        SignUpView signUpView = context.getBean(SignUpView.class);
        signUpView.setParentFrame((JFrame) loginView.getLoginPanel().getTopLevelAncestor());

        SignUpController signUpController = context.getBean(SignUpController.class);
        signUpController.initialize(signUpView, (JFrame) loginView.getLoginPanel().getTopLevelAncestor());

        signUpView.show();
    }

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
        initController();
    }

    public void setDesktopPane(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }

    @Autowired
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }
}