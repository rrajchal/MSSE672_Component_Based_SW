package com.topcard.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;

/**
 * The TopCardException class is a custom exception that extends Exception.
 * This exception is used to handle specific error scenarios in the TopCard application.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 */
public class TopCardException extends RuntimeException {

    private static final Logger logger = LogManager.getLogger(TopCardException.class);

    /**
     * Constructs a new TopCardException with the specified detail message.
     *
     * @param message the detail message
     */
    public TopCardException(String message) {
        super(message);
        logger.error(message);
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
        logger.error(message + "\n" + cause.getMessage());
        displayMessageDialog(message + "\n" + cause.getMessage());
    }

    /**
     * Constructs a new TopCardException with the cause of the exception.
     *
     * @param cause the cause of the exception
     */
    public TopCardException(Throwable cause) {
        super(cause);
        logger.error(cause.getMessage());
        displayMessageDialog(cause.getMessage());
    }

    /**
     * Displays a message dialog
     */
    private void displayMessageDialog(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
