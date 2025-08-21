package com.topcard.presentation.view;

import org.springframework.stereotype.Component;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;

/**
 * This class represents the view for the login screen.
 * It manages the layout and components of the login interface.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
public class LoginView {

    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginButton;
    private JLabel signUpLabel;

    /**
     * Constructor to initialize the login view.
     */
    public LoginView() {
        initializeWindow();
    }

    /**
     * Initializes the components of the login panel and sets up the layout.
     */
    public void initializeWindow() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        messageLabel = new JLabel("");
        gbc.gridx = 1;
        gbc.gridy = 4;
        loginPanel.add(messageLabel, gbc);

        // Inline Sign-Up Link
        signUpLabel = new JLabel("<html>Don't have an account? <a href=''>Sign up</a></html>");
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(signUpLabel, gbc);
    }

    // Getters for the components
    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JLabel getSignUpLabel() {
        return signUpLabel;
    }
}
