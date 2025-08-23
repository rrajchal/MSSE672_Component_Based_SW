package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.network.game.GameClient;
import com.topcard.network.game.GameMessage;
import com.topcard.presentation.common.InternalFrame;
import com.topcard.presentation.view.AddPlayerView;
import com.topcard.presentation.view.GameView;
import com.topcard.presentation.view.OptionsView;
import com.topcard.presentation.view.UpdateView;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the controller for the options view.
 * It manages the user interactions for the options screen.
 *
 * <p>
 * Author: Rajesh Rajchal
 *  Date: 08/15/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
public class OptionsController {

    private static final Logger logger = LogManager.getLogger(OptionsController.class);

    private OptionsView optionsView;
    private JDesktopPane desktopPane;
    private boolean isOnline; // boolean flag to track online status

    private PlayerManager playerManager;
    private Player player = null;

    // Inject controllers and views directly
    private final AddPlayerController addPlayerController;
    private final AddPlayerView addPlayerView;
    private final UpdateController updateController;
    private final UpdateView updateView;
    private final GameView gameView;

    @Autowired
    public OptionsController(PlayerManager playerManager, AddPlayerController addPlayerController, AddPlayerView addPlayerView,
                             UpdateController updateController, UpdateView updateView, GameView gameView) {
        this.playerManager = playerManager;
        this.addPlayerController = addPlayerController;
        this.addPlayerView = addPlayerView;
        this.updateController = updateController;
        this.updateView = updateView;
        this.gameView = gameView;
    }

    public void initialize(OptionsView optionsView, String username, boolean isOnline, JDesktopPane desktopPane) {
        this.optionsView = optionsView;
        this.isOnline = isOnline;
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
        logger.info("Initializing Option Controller");

        player = playerManager.getPlayerByUsername(username);

        optionsView.setAddPlayerButtonVisibility(player != null && player.isAdmin());

        // Conditional action listener based on online status
        if (isOnline) {
            optionsView.getPlayGameButton().addActionListener(e -> handlePlayGameOnline());
        } else {
            optionsView.getPlayGameButton().addActionListener(e -> handlePlayGameOffline());
        }

        optionsView.getUpdateButton().addActionListener(e -> handleUpdate(username));
        optionsView.getAddPlayerButton().addActionListener(e -> handleAddPlayer());
    }

    /**
     * Handles the event when the "Play Game" button is clicked in offline mode.
     */
    private void handlePlayGameOffline() {
        // Initialize JavaFX environment if not already initialized
        new JFXPanel();
        Platform.setImplicitExit(false);
        List<Player> players = getThreeRandomOpponentPlayers();

        Platform.runLater(() -> {
            try {
                gameView.setPlayers(players);
                Stage stage = new Stage();
                gameView.start(stage);
            } catch (Exception e) {
                logger.error("Failed to start the game view: " + e.getMessage());
                JOptionPane.showMessageDialog(optionsView.getOptionsPanel(), "Failed to start the game view.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Handles the event when the "Play Game" button is clicked in online mode.
     */
    private void handlePlayGameOnline() {
        new JFXPanel();
        Platform.setImplicitExit(false);
        GameMessage startGameMessage = new GameMessage("START_GAME", null);
        GameClient.getInstance().send(startGameMessage);
    }

    /**
     * Retrieves a list of three random players excluding the authenticated user.
     * The authenticated user is added as the first player.
     *
     * @return the list of four players (including the authenticated user)
     */
    private List<Player> getThreeRandomOpponentPlayers() {
        List<Player> allPlayers = playerManager.getAllPlayers();
        allPlayers.removeIf(p -> p.getUsername().equals(player.getUsername())); // remove the logged-in player from allPlayers
        List<Player> selectedPlayers = new ArrayList<>();
        selectedPlayers.add(player); // Add the logged-in player as the first player

        Collections.shuffle(allPlayers);

        // Select up to 3 opponents from the available list or create new temp players
        for (int i = 0; i < 3; i++) {
            if (i < allPlayers.size()) {
                selectedPlayers.add(allPlayers.get(i));
            } else {
                // Not enough players, generate a unique temporary username
                String baseUsername = "Bot" + (i + 1);
                String tempUsername = baseUsername;
                int suffix = 1;

                // Make sure the player tempUsername already does not exist
                while (playerManager.getPlayerByUsername(tempUsername) != null) {
                    tempUsername = baseUsername + "_" + suffix++;
                }
                LocalDate dob = LocalDate.of(1900, 1, 1);
                Player tempPlayer = new Player(tempUsername, "pass" + (i + 1), baseUsername,"Bot", dob);
                playerManager.addPlayer(tempPlayer);
                logger.warn("Not enough players in DB. Created temporary player: " + tempPlayer.getUsername());
                selectedPlayers.add(tempPlayer);
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
        updateView.initializeWindow();
        boolean isAdmin = player.isAdmin();
        updateController.initialize(updateView, username, isAdmin, desktopPane);
        InternalFrame.addInternalFrame(desktopPane, "Update", updateView.getUpdatePanel(), 700, 400, true);
        attachWindowListener(updateView);
    }

    /**
     * Handles the process for adding a new player when the Add Player button is clicked.
     * Opens the AddPlayerView and initializes the AddPlayerController.
     */
    private void handleAddPlayer() {
        disableOptionsView();
        addPlayerView.initializeWindow();
        addPlayerController.initialize(addPlayerView);
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

    @Autowired
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }
}
