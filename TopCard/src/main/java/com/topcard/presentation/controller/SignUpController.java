package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.Validation;
import com.topcard.presentation.view.SignUpView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class represents the controller for the sign-up view.
 * It manages the user interactions for signing up.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class SignUpController {

    private static final Logger logger = LogManager.getLogger(SignUpController.class);

    private final SignUpView signUpView;
    private final JFrame loginFrame;

    /**
     * Constructor to initialize the sign-up controller with the given sign-up view and login frame.
     *
     * @param signUpView the sign-up view
     * @param loginFrame the login frame
     */
    public SignUpController(SignUpView signUpView, JFrame loginFrame) {
        this.signUpView = signUpView;
        this.loginFrame = loginFrame;
        initController();
    }

    /**
     * Initializes the controller by setting up the action listeners.
     */
    private void initController() {
        logger.info("Initializing SignUp Controller");
        signUpView.getSignUpButton().addActionListener(e -> handleSignUp());
        // If SignUp view is closed, make the login View active again.
        signUpView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginFrame.setEnabled(true);
                loginFrame.toFront(); // Ensure the login frame stays on top
            }
        });
        loginFrame.setEnabled(false); // Disable the login frame when SignUpView is active
    }

    /**
     * Handles the sign-up process when the sign-up button is clicked.
     * It validates the input fields and adds a new player if all fields are valid.
     */
    private void handleSignUp() {
        if (!signUpView.getTermsCheckBox().isSelected()) {
            JOptionPane.showMessageDialog(signUpView.getSignUpDialog(), Constants.AGREEMENT_CHECKBOX_MSG, Constants.AGREEMENT_REQUIRED, JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] textFields = {
                signUpView.getFirstNameField(), signUpView.getLastNameField(),
                signUpView.getUsernameField(), signUpView.getPasswordField(), signUpView.getDateOfBirthField()
        };

        String[] errorMessages = {
                Constants.FIRST_NAME_CANNOT_HAVE_SPACES, Constants.LAST_NAME_CANNOT_HAVE_SPACES,
                Constants.USERNAME_CANNOT_HAVE_SPACES, Constants.PASSWORD_CANNOT_HAVE_SPACES, Constants.REQUIRED
        };

        boolean isValid = Validation.validateFields(textFields, errorMessages);

        if (isValid) {
            // Check if passwords match
            String password = new String(signUpView.getPasswordField().getPassword());
            String retypePassword = new String(signUpView.getRetypePasswordField().getPassword());
            if (!password.equals(retypePassword)) {
                JOptionPane.showMessageDialog(signUpView.getSignUpDialog(), Constants.PASSWORD_NOT_MATCH, Constants.ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate date of birth format
            LocalDate dateOfBirth;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                dateOfBirth = LocalDate.parse(signUpView.getDateOfBirthField().getText().trim(), formatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(signUpView.getSignUpDialog(), Constants.INVALID_DATE, Constants.ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add player
            PlayerManager playerManager = new PlayerManager();
            Player newPlayer = new Player(
                    signUpView.getUsernameField().getText(),
                    password,
                    signUpView.getFirstNameField().getText(),
                    signUpView.getLastNameField().getText(),
                    dateOfBirth
            );

            playerManager.addPlayer(newPlayer);
            JOptionPane.showMessageDialog(signUpView.getSignUpDialog(), Constants.SIGN_UP_SUCCESS, Constants.SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            signUpView.getSignUpDialog().dispose(); // Close the SignUpView
            loginFrame.setEnabled(true); // Re-enable the login frame
        }
    }
}
