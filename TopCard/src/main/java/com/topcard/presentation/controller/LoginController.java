package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.network.GameClient;
import com.topcard.network.SocketGameController;
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
import javax.swing.*;

/**
 * This class represents the controller for the login view.
 * It manages the user interactions for logging in.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
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
     * It gathers the user inputs and initiates the authentication process.
     */
    private void handleLogin() {
        String username = loginView.getUsernameField().getText();
        String password = new String(loginView.getPasswordField().getPassword());

        if (validateInputs(loginView.getUsernameField(), username, Constants.USERNAME_CANNOT_HAVE_SPACES) &&
                validateInputs(loginView.getPasswordField(), password, Constants.PASSWORD_CANNOT_HAVE_SPACES) &&
                authenticate(username, password)) {

            PlayerManager playerManager = new PlayerManager();
            Player loggedInPlayer = playerManager.getPlayerByUsername(username);

            // Connect to multiplayer game
            try {
                //new SocketGameController(loggedInPlayer, "localhost");
                GameClient.getInstance().connect("localhost", loggedInPlayer);
            } catch (IOException e) {
                logger.error("Could not connect to game server: " + e.getMessage());
                JOptionPane.showMessageDialog(loginView.getLoginPanel(),
                        "Failed to join multiplayer server.",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Close login frame and show Options
            JInternalFrame loginFrame = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, loginView.getLoginPanel());
            if (loginFrame != null) {
                loginFrame.dispose();
            }

            OptionsView optionsView = new OptionsView();
            new OptionsController(optionsView, username, desktopPane);
            InternalFrame.addInternalFrame(desktopPane, "Choose an Option", optionsView.getOptionsPanel(), 400, 200, false);
        } else {
            loginView.getMessageLabel().setForeground(Color.RED);
            loginView.getMessageLabel().setText(Constants.INVALID_USERNAME_OR_PASSWORD);
        }
    }

    /*
    private void handleLogin() {
        String username = loginView.getUsernameField().getText();
        String password = new String(loginView.getPasswordField().getPassword());

        if (validateInputs(loginView.getUsernameField(), username, Constants.USERNAME_CANNOT_HAVE_SPACES) &&
            validateInputs(loginView.getPasswordField(), password, Constants.PASSWORD_CANNOT_HAVE_SPACES) &&
            authenticate(username, password)) {

            // Close the LoginView internal frame
            JInternalFrame loginInternalFrame = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, loginView.getLoginPanel());
            if (loginInternalFrame != null) {
                loginInternalFrame.dispose();
            }

            // Initialize and add the options view as an internal frame
            OptionsView optionsView = new OptionsView();
            new OptionsController(optionsView, username, desktopPane);

            desktopPane.add(optionsView);
            desktopPane.revalidate();
            desktopPane.repaint();

            // Add internal frame
            InternalFrame.addInternalFrame(desktopPane, "Choose an Option", optionsView.getOptionsPanel(), 400, 200, false);
        } else {
            loginView.getMessageLabel().setForeground(Color.RED);
            loginView.getMessageLabel().setText(Constants.INVALID_USERNAME_OR_PASSWORD);
        }
    }
     */

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
