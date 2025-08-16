package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.network.GameClient;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.InternalFrame;
import com.topcard.presentation.common.Validation;
import com.topcard.presentation.view.LoginView;
import com.topcard.presentation.view.OptionsView;
import com.topcard.presentation.view.SignUpView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
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
public class LoginController extends JFrame {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final LoginView loginView;
    private final JDesktopPane desktopPane;
    /**
     * Constructor to initialize the login controller with the given login view.
     *
     * @param loginView the login view
     */
    public LoginController(LoginView loginView, JDesktopPane desktopPane) {
        this.loginView = loginView;
        this.desktopPane = desktopPane;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initController();
    }

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

            PlayerManager playerManager = new PlayerManager();
            Player loggedInPlayer = playerManager.getPlayerByUsername(username);

            // Attempt to connect to the server first
            if (isServerRunning()) {
                logger.info("Server is available. Attempting to join online game.");
                try {
                    GameClient.getInstance().connect("localhost", loggedInPlayer);
                    launchOptionsView(username, true); // Pass a flag indicating online mode
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
        OptionsView optionsView = new OptionsView();
        new OptionsController(optionsView, username, isOnline, desktopPane);
        InternalFrame.addInternalFrame(desktopPane, "Choose an Option", optionsView.getOptionsPanel(), 400, 200, false);
    }

    /**
     * Checks if the game server is reachable by attempting a socket connection.
     * This method acts as a client-side replacement for a server-side method.
     *
     * @return true if a connection can be established, false otherwise.
     */
    private boolean isServerRunning() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", Constants.PORT), 50); // timeout
            return true;
        } catch (SocketTimeoutException e) {
            logger.warn("Server is unreachable: connection timed out.");
            return false;
        } catch (IOException e) {
            logger.warn("Server is not available at " + "localhost" + ":" + Constants.PORT + " (" + e.getMessage() + ")");
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
        PlayerManager playerManager = new PlayerManager();
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
        SignUpView signUpView = new SignUpView((JFrame) loginView.getLoginPanel().getTopLevelAncestor());
        new SignUpController(signUpView, (JFrame) loginView.getLoginPanel().getTopLevelAncestor());
        signUpView.show();
    }
}