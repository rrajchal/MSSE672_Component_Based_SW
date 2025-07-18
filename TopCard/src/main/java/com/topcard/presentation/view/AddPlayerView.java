package com.topcard.presentation.view;

import com.topcard.presentation.common.Constants;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

public class AddPlayerView extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField dateOfBirthField;
    private JButton addPlayerButton;

    // Components for XML upload/download
    private JTextField xmlFilePathField;  // This is clickable
    private JButton uploadXmlButton;    // Button to trigger XML upload
    private JButton downloadXmlButton;  // Button for downloading XML

    public AddPlayerView() {
        setTitle("Add Player");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the monitor
        initComponents();
    }

    private void initComponents() {
        JPanel addPlayerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel firstNameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPlayerPanel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addPlayerPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPlayerPanel.add(lastNameLabel, gbc);

        lastNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addPlayerPanel.add(lastNameField, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPlayerPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addPlayerPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        addPlayerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        addPlayerPanel.add(passwordField, gbc);

        JLabel dateOfBirthLabel = new JLabel("Date of Birth (MM/DD/YYYY):");
        gbc.gridx = 0;
        gbc.gridy = 4;
        addPlayerPanel.add(dateOfBirthLabel, gbc);

        dateOfBirthField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        addPlayerPanel.add(dateOfBirthField, gbc);

        addPlayerButton = new JButton("Add Single Player");
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        addPlayerPanel.add(addPlayerButton, gbc);

        JLabel messageLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        addPlayerPanel.add(messageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        addPlayerPanel.add(new JLabel("-------------------- OR --------------------"), gbc);

        // --- New XML Upload Fields ---
        JLabel xmlLabel = new JLabel("Upload Players (XML):");
        gbc.gridx = 0;
        gbc.gridy = 7;
        addPlayerPanel.add(xmlLabel, gbc);

        xmlFilePathField = new JTextField(Constants.CLICK_TO_UPLOAD_XML_FILE, 15);
        gbc.gridx = 1;
        gbc.gridy = 7;
        xmlFilePathField.setEditable(false);
        addPlayerPanel.add(xmlFilePathField, gbc);

        uploadXmlButton = new JButton("Upload XML Players");
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.CENTER;

        addPlayerPanel.add(uploadXmlButton, gbc);

        // --- XML Download Button ---
        gbc.gridx = 1;
        gbc.gridy = 10;
        addPlayerPanel.add(new JLabel(Constants.EMPTY), gbc);


        gbc.gridx = 1;
        gbc.gridy = 11;
        downloadXmlButton = new JButton("Download All Players as XML");
        addPlayerPanel.add(downloadXmlButton, gbc);

        add(addPlayerPanel);
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

    public JButton getAddPlayerButton() {
        return addPlayerButton;
    }

    public JTextField getXmlFilePathField() {
        return xmlFilePathField;
    }
    public JButton getUploadXmlButton() {
        return uploadXmlButton;
    }

    public JButton getDownloadXmlButton() {
        return downloadXmlButton;
    }
}
