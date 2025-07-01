package com.topcard.exceptions;

import com.topcard.debug.Debug;

import javax.swing.JOptionPane;

/**
 * The TopCardException class is a custom exception that extends Exception.
 * This exception is used to handle specific error scenarios in the TopCard application.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 12/13/2024
 * Subject: MSSE 670 Object Oriented Software Construction
 */
public class TopCardException extends RuntimeException {

    /**
     * Constructs a new TopCardException with the specified detail message.
     *
     * @param message the detail message
     */
    public TopCardException(String message) {
        super(message);
        Debug.error(message);
        displayMessageDialog(message);
    }

    /**
     * Constructs a new TopCardException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public TopCardException(String message, Throwable cause) {
        super(message, cause);
        Debug.error(message + "\n" + cause.getMessage());
        displayMessageDialog(message + "\n" + cause.getMessage());
    }

    /**
     * Constructs a new TopCardException with the cause of the exception.
     *
     * @param cause the cause of the exception
     */
    public TopCardException(Throwable cause) {
        super(cause);
        Debug.error(cause.getMessage());
        displayMessageDialog(cause.getMessage());
    }

    /**
     * Displays a message dialog
     */
    private void displayMessageDialog(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
