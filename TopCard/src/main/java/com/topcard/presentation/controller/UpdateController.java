package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.Validation;
import com.topcard.presentation.view.UpdateView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;

/**
 * This class represents the controller for the update view.
 * It manages the user interactions for updating player information.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
public class UpdateController {

    private static final Logger logger = LogManager.getLogger(UpdateController.class);

    private UpdateView updateView;
    private PlayerManager playerManager;
    private boolean isAdmin;
    private String username;
    private JDesktopPane desktopPane;

//    /**
//     * Constructor to initialize the update controller with the given update view and admin status.
//     *
//     * @param updateView the update view
//     * @param isAdmin whether the user is an admin
//     */
//    public UpdateController(UpdateView updateView, String username, boolean isAdmin, JDesktopPane desktopPane) {
//        this.updateView = updateView;
//        this.username = username;
//        this.desktopPane = desktopPane;
//        this.isAdmin = isAdmin;
//        initController();
//    }

    public void initialize(UpdateView updateView, String username, boolean isAdmin, JDesktopPane desktopPane) {
        this.updateView = updateView;
        this.username = username;
        this.isAdmin = isAdmin;
        this.desktopPane = desktopPane;
        initController();
    }

    /**
     * Initializes the controller by setting up the action listeners.
     */
    private void initController() {
        logger.info("Initializing UpdateController");
        if (isAdmin) {
            updateView.getSearchButton().addActionListener(e -> handleSearch());
        } else {
            populateUserInfo(username);
            updateView.getSearchLevel().setVisible(false);
            updateView.getSearchField().setVisible(false);
            updateView.getSearchButton().setVisible(false);
            updateView.getIsAdminLabel().setVisible(false);
            updateView.getPointsField().setEnabled(false);
            updateView.getIsAdminCheckBox().setVisible(false);
        }
        updateView.getUsernameField().setEnabled(false);
        updateView.getUpdateButton().addActionListener(e -> handleUpdate());
    }

    /**
     * Populate user information in the options view
     */
    private void populateUserInfo(String username) {
        Player player = playerManager.getPlayerByUsername(username);
        if (player != null) {
            updateView.getIdField().setText(String.valueOf(player.getPlayerId()));
            updateView.getFirstNameField().setText(player.getFirstName());
            updateView.getLastNameField().setText(player.getLastName());
            updateView.getUsernameField().setText(player.getUsername());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
            updateView.getDateOfBirthField().setText(player.getDateOfBirth().format(formatter));
            updateView.getPointsField().setText(String.valueOf(player.getPoints()));
        }
    }

    /**
     * Handles the search process when the search button is clicked.
     * It looks up the player by ID and populates the fields if the player is found.
     */
    private void handleSearch() {
        int playerId;
        try {
            playerId = Integer.parseInt(updateView.getSearchField().getText().trim());
        } catch (NumberFormatException e) {
            updateView.getMessageLabel().setForeground(Color.RED);
            updateView.getMessageLabel().setText(Constants.NO_PLAYER_FOUND);
            return;
        }

        Player player = playerManager.getPlayerById(playerId);

        if (player != null) {
            updateView.getIdField().setText(String.valueOf(player.getPlayerId()));
            updateView.getFirstNameField().setText(player.getFirstName());
            updateView.getLastNameField().setText(player.getLastName());
            updateView.getUsernameField().setText(player.getUsername());
            updateView.getPasswordField().setText(player.getPassword());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
            updateView.getDateOfBirthField().setText(player.getDateOfBirth().format(formatter));
            updateView.getPointsField().setText(String.valueOf(player.getPoints()));
            updateView.getIsAdminCheckBox().setSelected(player.isAdmin());
            updateView.getMessageLabel().setText("");
        } else {
            updateView.getIdField().setText("");
            updateView.getFirstNameField().setText("");
            updateView.getLastNameField().setText("");
            updateView.getUsernameField().setText("");
            updateView.getPasswordField().setText("");
            updateView.getDateOfBirthField().setText("");
            updateView.getPointsField().setText("");
            updateView.getIsAdminCheckBox().setSelected(false);
            updateView.getMessageLabel().setText("");
            updateView.getMessageLabel().setForeground(Color.RED);
            updateView.getMessageLabel().setText(Constants.NO_PLAYER_FOUND);
        }
    }

    /**
     * Handles the update process when the update button is clicked.
     * It validates the input fields and updates the player information if all fields are valid.
     */
    private void handleUpdate() {
        Object[] textFields = {updateView.getFirstNameField(), updateView.getLastNameField(),
                updateView.getUsernameField(), updateView.getPasswordField(), updateView.getDateOfBirthField()};

        String[] errorMessages = { Constants.FIRST_NAME_CANNOT_HAVE_SPACES, Constants.LAST_NAME_CANNOT_HAVE_SPACES,
                Constants.USERNAME_CANNOT_HAVE_SPACES, Constants.PASSWORD_CANNOT_HAVE_SPACES, Constants.REQUIRED};

        boolean isValid = Validation.validateFields(textFields, errorMessages);

        if (isValid) {
            // Validate date of birth format
            LocalDate dateOfBirth;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                dateOfBirth = LocalDate.parse(updateView.getDateOfBirthField().getText().trim(), formatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(updateView, Constants.INVALID_DATE,
                        Constants.ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update player information
            Player updatedPlayer = new Player(
                    updateView.getUsernameField().getText(),
                    new String(updateView.getPasswordField().getPassword()),
                    updateView.getFirstNameField().getText(),
                    updateView.getLastNameField().getText(),
                    dateOfBirth);

            try {
                int points = Integer.parseInt(updateView.getPointsField().getText().trim());
                updatedPlayer.setPoints(points);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(updateView, Constants.INVALID_POINT, Constants.ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isAdmin) {
                updatedPlayer.setAdmin(updateView.getIsAdminCheckBox().isSelected());
            }
            updatedPlayer.setPlayerId(Integer.parseInt(updateView.getIdField().getText()));
            playerManager.updateProfile(updatedPlayer);
            JOptionPane.showMessageDialog(updateView, Constants.UPDATED, Constants.SUCCESS, JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(updateView, Constants.HOVER_MESSAGE, Constants.FAILED, JOptionPane.INFORMATION_MESSAGE);
        }

        updateView.dispose();
    }
}
