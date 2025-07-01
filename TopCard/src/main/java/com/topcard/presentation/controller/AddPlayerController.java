package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.Validation;
import com.topcard.presentation.view.AddPlayerView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;

/**
 * This class represents the controller for the add player view.
 * It manages the user interactions for adding a new player.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class AddPlayerController {

    private static final Logger logger = LogManager.getLogger(AddPlayerController.class);

    private final AddPlayerView addPlayerView;
    private final PlayerManager playerManager;

    /**
     * Constructor to initialize the add player controller with the given add player view.
     *
     * @param addPlayerView the add player view
     */
    public AddPlayerController(AddPlayerView addPlayerView) {
        this.addPlayerView = addPlayerView;
        this.playerManager = new PlayerManager();
        initController();
    }

    /**
     * Initializes the controller by setting up the action listeners.
     */
    private void initController() {
        logger.info("Initializing AddPlayer Controller");
        addPlayerView.getAddPlayerButton().addActionListener(e -> handleAddPlayer());
    }

    /**
     * Handles the add player process when the add player button is clicked.
     * It validates the input fields and adds a new player if all fields are valid.
     */
    private void handleAddPlayer() {
        Object[] textFields = {addPlayerView.getFirstNameField(), addPlayerView.getLastNameField(),
            addPlayerView.getUsernameField(), addPlayerView.getPasswordField(), addPlayerView.getDateOfBirthField()};

        String[] errorMessages = { Constants.FIRST_NAME_CANNOT_HAVE_SPACES, Constants.LAST_NAME_CANNOT_HAVE_SPACES,
                Constants.USERNAME_CANNOT_HAVE_SPACES, Constants.PASSWORD_CANNOT_HAVE_SPACES, Constants.REQUIRED};

        boolean isValid = Validation.validateFields(textFields, errorMessages);

        if (isValid) {
            // Validate date of birth format
            LocalDate dateOfBirth;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                dateOfBirth = LocalDate.parse(addPlayerView.getDateOfBirthField().getText().trim(), formatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(addPlayerView, Constants.INVALID_DATE, Constants.ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add player
            Player newPlayer = new Player(
                    addPlayerView.getUsernameField().getText(),
                    new String(addPlayerView.getPasswordField().getPassword()), // Update the password
                    addPlayerView.getFirstNameField().getText(),
                    addPlayerView.getLastNameField().getText(),
                    dateOfBirth
            );

            boolean playerAdded = playerManager.addPlayer(newPlayer);
            if (playerAdded) {
                JOptionPane.showMessageDialog(addPlayerView, Constants.PLAYER_ADDED, Constants.SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(addPlayerView, Constants.PLAYER_NOT_ADDED, Constants.FAILED, JOptionPane.INFORMATION_MESSAGE);
            }

            addPlayerView.dispose(); // Close the AddPlayerView
        }
    }
}
