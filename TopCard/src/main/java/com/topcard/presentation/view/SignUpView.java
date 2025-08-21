package com.topcard.presentation.view;

import com.topcard.presentation.common.Constants;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

/**
 * This class represents the view for the sign-up screen.
 * It manages the layout and components of the sign-up interface.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
public class SignUpView {

    // Dialog for displaying the sign-up view
    private JDialog signUpDialog;

    // Text fields for user input
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField retypePasswordField;
    private JTextField dateOfBirthField;
    private JTextField bonusPointField;
    private JCheckBox termsCheckBox;
    private JButton signUpButton;
    private JLabel loginLink;

//    /**
//     * Constructor to initialize the sign-up view.
//     */
//    public SignUpView(JFrame parentFrame) {
//        initComponents(parentFrame);
//    }

    public void setParentFrame(JFrame parentFrame) {
        initComponents(parentFrame);
    }

    /**
     * Initializes the components of the sign-up panel and sets up the layout.
     */
    private void initComponents(JFrame parentFrame) {
        signUpDialog = new JDialog(parentFrame, "Sign Up", true);
        signUpDialog.setSize(450, 500);
        signUpDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        signUpDialog.setLocationRelativeTo(null); // Center the dialog on the monitor

        // Panel for organizing the sign-up components
        JPanel signUpPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5); // Padding

        // First Name
        JLabel firstNameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        signUpPanel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        signUpPanel.add(firstNameField, gbc);

        // Last Name
        JLabel lastNameLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        signUpPanel.add(lastNameLabel, gbc);

        lastNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        signUpPanel.add(lastNameField, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        signUpPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        signUpPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        signUpPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        signUpPanel.add(passwordField, gbc);

        // Re-type Password
        JLabel retypePasswordLabel = new JLabel("Re-type Password:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        signUpPanel.add(retypePasswordLabel, gbc);

        retypePasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        signUpPanel.add(retypePasswordField, gbc);

        // Date of Birth
        JLabel dateOfBirthLabel = new JLabel("Date of Birth (MM/DD/YYYY):");
        gbc.gridx = 0;
        gbc.gridy = 5;
        signUpPanel.add(dateOfBirthLabel, gbc);

        dateOfBirthField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 5;
        signUpPanel.add(dateOfBirthField, gbc);

        // Signup Bonus Point
        JLabel bonusPointLabel = new JLabel("Signup Bonus Point:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        signUpPanel.add(bonusPointLabel, gbc);

        bonusPointField = new JTextField("100", 15);
        bonusPointField.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 6;
        signUpPanel.add(bonusPointField, gbc);

        // Terms and Conditions Checkbox
        termsCheckBox = new JCheckBox(Constants.I_AGREE_TERMS);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        signUpPanel.add(termsCheckBox, gbc);
        gbc.gridwidth = 1;

        // Sign Up button
        signUpButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        signUpPanel.add(signUpButton, gbc);

        // Login link
        loginLink = new JLabel(Constants.ALREADY_HAVE_ACCOUNT);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        signUpPanel.add(loginLink, gbc);
        gbc.gridwidth = 1;

        signUpDialog.add(signUpPanel);

        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                signUpDialog.dispose(); // Close the SignUpView
                parentFrame.setEnabled(true); // Re-enable the login frame
                parentFrame.toFront(); // Ensure the login frame stays on top
            }
        });
    }

    // Getters for the components
    public JDialog getSignUpDialog() {
        return signUpDialog;
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

    public JPasswordField getRetypePasswordField() {
        return retypePasswordField;
    }

    public JTextField getDateOfBirthField() {
        return dateOfBirthField;
    }

    public JTextField getBonusPointField() {
        return bonusPointField;
    }

    public JCheckBox getTermsCheckBox() {
        return termsCheckBox;
    }

    public JButton getSignUpButton() {
        return signUpButton;
    }

    public JLabel getLoginLink() {
        return loginLink;
    }

    /**
     * Adds a window listener to the sign-up dialog.
     *
     * @param listener the window listener to be added to the sign-up dialog
     */
    public void addWindowListener(WindowAdapter listener) {
        signUpDialog.addWindowListener(listener);
    }

    /**
     * Makes the sign-up dialog visible to the user.
     */
    public void show() {
        signUpDialog.setVisible(true);
    }
}
