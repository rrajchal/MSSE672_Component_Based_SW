package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.debug.Debug;
import com.topcard.domain.Player;
import com.topcard.presentation.common.InternalFrame;
import com.topcard.presentation.view.AddPlayerView;
import com.topcard.presentation.view.GameView;
import com.topcard.presentation.view.OptionsView;
import com.topcard.presentation.view.UpdateView;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the controller for the options view.
 * It manages the user interactions for the options screen.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 12/07/2024
 * Subject: MSSE 670 Object Oriented Software Construction
 * </p>
 */
public class OptionsController {

    private final OptionsView optionsView;
    private final JDesktopPane desktopPane;

    private Player player = null;

    /**
     * Constructor to initialize the options controller with the given options view.
     *
     * @param optionsView the options view
     * @param username the username of the authenticated player
     * @param desktopPane the desktop pane to manage internal frames
     */
    public OptionsController(OptionsView optionsView, String username, JDesktopPane desktopPane) {
        this.optionsView = optionsView;
        this.desktopPane = desktopPane;
        initController(username);
    }

    /**
     * Initializes the controller by setting up the visibility and action listeners.
     * It determines whether the authenticated user is an admin and sets the visibility
     * of the Add Player button accordingly.
     *
     * @param username the username of the authenticated player
     */
    private void initController(String username) {
        Debug.info("Initializing Option Controller");
        PlayerManager playerManager = new PlayerManager();
        player = playerManager.getPlayerByUsername(username);

        if (player != null && player.isAdmin()) {
            optionsView.setAddPlayerButtonVisibility(true);
        } else {
            optionsView.setAddPlayerButtonVisibility(false);
        }
        optionsView.getPlayGameButton().addActionListener(e -> handlePlayGame());
        optionsView.getUpdateButton().addActionListener(e -> handleUpdate(username));
        optionsView.getAddPlayerButton().addActionListener(e -> handleAddPlayer());
    }

    /**
     * Handles the event when the "Play Game" button is clicked.
     * It initializes the JavaFX environment, retrieves a list of players, and starts the game view.
     */
    private void handlePlayGame() {
        // Initialize JavaFX environment if not already initialized
        new JFXPanel();
        Platform.setImplicitExit(false);
        List<Player> players = getThreeRandomPlayers();

        Platform.runLater(() -> {
            try {
                GameView gameView = new GameView(players);
                Stage stage = new Stage();
                gameView.start(stage);
            } catch (Exception e) {
                Debug.error("Failed to start the game view: " + e.getMessage());
                JOptionPane.showMessageDialog(optionsView, "Failed to start the game view.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Retrieves a list of three random players excluding the authenticated user.
     * The authenticated user is added as the first player.
     *
     * @return the list of four players (including the authenticated user)
     */
    private List<Player> getThreeRandomPlayers() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> allPlayers = playerManager.getAllPlayers(); // Assuming this method exists
        List<Player> selectedPlayers = new ArrayList<>();
        selectedPlayers.add(player); // Add the logged-in player as the first player

        // Randomly select three other players from the list
        Random rand = new Random();
        while (selectedPlayers.size() < 4) {
            Player randomPlayer = allPlayers.get(rand.nextInt(allPlayers.size()));
            if (!selectedPlayers.contains(randomPlayer)) {
                selectedPlayers.add(randomPlayer);
            }
        }

        return selectedPlayers;
    }

    /**
     * Handles the update process when the update button is clicked.
     * Opens the UpdateView and initializes the UpdateController.
     *
     * @param username the username of the authenticated player
     */
    private void handleUpdate(String username) {
        //disableOptionsView();
        UpdateView updateView = new UpdateView();
        boolean isAdmin = player.isAdmin();
        new UpdateController(updateView, username, isAdmin, desktopPane); // Pass the desktopPane
        InternalFrame.addInternalFrame(desktopPane, "Update", updateView.getUpdatePanel(), 700, 400, true);
        attachWindowListener(updateView);
    }

    /**
     * Handles the process for adding a new player when the Add Player button is clicked.
     * Opens the AddPlayerView and initializes the AddPlayerController.
     */
    private void handleAddPlayer() {
        disableOptionsView();
        AddPlayerView addPlayerView = new AddPlayerView();
        new AddPlayerController(addPlayerView);
        attachWindowListener(addPlayerView);
        addPlayerView.setVisible(true);
    }

    /**
     * Attaches a window listener to the given JFrame.
     *
     * @param frame the JFrame to which the window listener is attached
     */
    private void attachWindowListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                enableOptionsView();
            }
        });
    }

    /**
     * Disables the options view and fades it.
     */
    private void disableOptionsView() {
        optionsView.getUpdateButton().setEnabled(false);
        optionsView.getAddPlayerButton().setEnabled(false);
        //fadeTheWindow(0.5f);
    }

    /**
     * Enables the options view and restores its opacity.
     */
    private void enableOptionsView() {
        optionsView.getUpdateButton().setEnabled(true);
        optionsView.getAddPlayerButton().setEnabled(true);
        //fadeTheWindow(1.0f);
    }

    /**
     * Fades the options view window to the specified opacity level.
     *
     * @param opacity the desired opacity level (between 0.0 and 1.0)
     */
    private void fadeTheWindow(float opacity) {
        Window window = SwingUtilities.getWindowAncestor(this.optionsView);
        if (window != null) {
            window.setOpacity(opacity);
        }
    }
}
