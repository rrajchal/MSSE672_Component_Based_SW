package com.topcard.presentation.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;

/**
 * This class represents the view for the options screen.
 * It manages the layout and components of the options interface.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class OptionsView extends JPanel {

    // Panel for organizing the options components
    private JPanel optionsPanel;

    // Button for playing the game
    private JButton playGameButton;

    // Button for adding a player, visible only if the user is an admin
    private JButton addPlayerButton;

    // Button for updating the user's profile
    private JButton updateButton;

    /**
     * Constructor to initialize the options view.
     */
    public OptionsView() {
        initComponents();
    }

    /**
     * Initializes the components of the options panel and sets up the layout.
     */
    private void initComponents() {
        // Option Panel to populate all widgets
        optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Play Game button
        playGameButton = new JButton("Play Game");
        gbc.gridx = 0;
        gbc.gridy = 0;
        optionsPanel.add(playGameButton, gbc);

        // Add Player button
        addPlayerButton = new JButton("Add Players");
        gbc.gridx = 1;
        gbc.gridy = 0;
        optionsPanel.add(addPlayerButton, gbc);

        // Update button
        updateButton = new JButton("Update");
        gbc.gridx = 2;
        gbc.gridy = 0;
        optionsPanel.add(updateButton, gbc);

        // Add optionsPanel to the main panel
        this.setLayout(new BorderLayout());
        this.add(optionsPanel, BorderLayout.CENTER);
    }

    public JPanel getOptionsPanel() {
        return this.optionsPanel;
    }

    public JButton getPlayGameButton() {
        return playGameButton;
    }

    public JButton getAddPlayerButton() {
        return addPlayerButton;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    /**
     * Sets the visibility of the Add Player button.
     *
     * @param isVisible true if the button should be visible, false otherwise
     */
    public void setAddPlayerButtonVisibility(boolean isVisible) {
        addPlayerButton.setVisible(isVisible);
    }
}
