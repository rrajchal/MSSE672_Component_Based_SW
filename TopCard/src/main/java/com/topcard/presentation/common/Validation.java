package com.topcard.presentation.common;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Color;

/**
 * The Validation class provides methods to validate user inputs in the application.
 * It ensures that input fields meet the defined validation criteria
 * and sets appropriate error messages when validation fails.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 11/30/2024
 * Subject: MSSE 670 Object Oriented Software Construction
 * </p>
 */
public class Validation {

    /**
     * Validates if the given text field contains any spaces.
     * If spaces are found, sets the error message to the text field.
     *
     * @param textField the text field to validate
     * @param text the text to check for spaces
     * @param errorMessage the error message to display if spaces are found
     * @return true if valid, false otherwise
     */
    public static boolean validateForSpaces(JTextField textField, String text, String errorMessage) {
        if (text.contains(" ")) {
            textField.setBackground(Color.PINK);
            textField.setToolTipText(errorMessage);
            return false;
        } else {
            textField.setBackground(Color.WHITE);
            textField.setToolTipText(null);
            return true;
        }
    }

    /**
     * Validates a set of fields for spaces and required inputs.
     *
     * @param fields an array of JTextFields or JPasswordFields to validate
     * @param errorMessages an array of corresponding error messages for each field
     * @return true if all fields are valid, false otherwise
     */
    public static boolean validateFields(Object[] fields, String[] errorMessages) {
        boolean allValid = true;

        for (int i = 0; i < fields.length; i++) {
            JTextField textField;
            if (fields[i] instanceof JPasswordField) {
                textField = (JPasswordField) fields[i];
            } else {
                textField = (JTextField) fields[i];
            }

            boolean fieldValid = true;

            if (!validateForSpaces(textField, textField.getText(), errorMessages[i])) {
                fieldValid = false;
            }

            if (textField.getText().trim().isEmpty()) {
                textField.setToolTipText(Constants.REQUIRED);
                fieldValid = false;
            }

            if (!fieldValid) {
                textField.setBackground(Color.PINK);
                allValid = false;
            } else {
                textField.setBackground(Color.WHITE);
            }
        }
        return allValid;
    }
}
