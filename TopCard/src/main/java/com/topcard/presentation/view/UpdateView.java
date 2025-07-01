package com.topcard.presentation.view;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * This class represents the view for the update screen.
 * It manages the layout and components of the update interface.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class UpdateView extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JLabel messageLabel;
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField dateOfBirthField;
    private JTextField pointsField;
    private JLabel isAdminLabel;
    private JCheckBox isAdminCheckBox;
    private JButton updateButton;
    private JPanel updatePanel;

    /**
     * Constructor to initialize the update view.
     */
    public UpdateView() {
        setTitle("Update Player");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    /**
     * Initializes the components of the update panel and sets up the layout.
     */
    private void initComponents() {
        updatePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Search section for admin users
        JLabel searchLabel = new JLabel("Player ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        updatePanel.add(searchLabel, gbc);

        searchField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        updatePanel.add(searchField, gbc);

        searchButton = new JButton("Look Up");
        gbc.gridx = 1;
        gbc.gridy = 1;
        updatePanel.add(searchButton, gbc);

        messageLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        updatePanel.add(messageLabel, gbc);
        gbc.gridwidth = 1;

        // Player information fields
        JLabel idLabel = new JLabel("ID:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        updatePanel.add(idLabel, gbc);

        idField = new JTextField(15);
        idField.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        updatePanel.add(idField, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        updatePanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        updatePanel.add(usernameField, gbc);

        JLabel firstNameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        updatePanel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 5;
        updatePanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        updatePanel.add(lastNameLabel, gbc);

        lastNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 6;
        updatePanel.add(lastNameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        updatePanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 7;
        updatePanel.add(passwordField, gbc);

        JLabel dateOfBirthLabel = new JLabel("Date of Birth (MM/DD/YYYY):");
        gbc.gridx = 0;
        gbc.gridy = 8;
        updatePanel.add(dateOfBirthLabel, gbc);

        dateOfBirthField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 8;
        updatePanel.add(dateOfBirthField, gbc);

        JLabel pointLabel = new JLabel("Point");
        gbc.gridx = 0;
        gbc.gridy = 9;
        updatePanel.add(pointLabel, gbc);

        pointsField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 9;
        updatePanel.add(pointsField, gbc);

        isAdminLabel = new JLabel("Is Admin:");
        gbc.gridx = 0;
        gbc.gridy = 10;
        updatePanel.add(isAdminLabel, gbc);

        isAdminCheckBox = new JCheckBox();
        gbc.gridx = 1;
        gbc.gridy = 10;
        updatePanel.add(isAdminCheckBox, gbc);

        // Update button
        updateButton = new JButton("Update");
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.CENTER;
        updatePanel.add(updateButton, gbc);

        add(updatePanel);
    }

    // Getters for the components
    public JPanel getUpdatePanel() {
        return updatePanel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }

    public JTextField getIdField() {
        return idField;
    }

    public JLabel getIsAdminLabel() {
        return isAdminLabel;
    }

    public JTextField getFirstNameField() {
        return firstNameField;
    }

    public JTextField getLastNameField() {
        return lastNameField;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JTextField getDateOfBirthField() {
        return dateOfBirthField;
    }

    public JTextField getPointsField() {
        return pointsField;
    }

    public JCheckBox getIsAdminCheckBox() {
        return isAdminCheckBox;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }
}
