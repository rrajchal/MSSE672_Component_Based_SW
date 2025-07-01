package com.topcard.debug;

/**
 * Class for logging debug, warning and error messages.
 * Allows enabling or disabling debug mode.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 12/13/2024
 * Subject: MSSE 670 Object Oriented Software construction
 */
public class Debug {
    private static boolean debugMode = false;
    // ANSI escape code for red text
    private static final String RED_TEXT = "\u001B[31m";
    // ANSI escape code for yellow text
    private static final String YELLOW_TEXT = "\u001B[33m";
    // Reset code to return to default color
    private static final String DEFAULT_TEXT = "\u001B[0m";

    /**
     * Sets the debug mode.
     *
     * @param debugMode true to enable debug mode, false to disable
     */
    public static void setDebugMode(boolean debugMode) {
        Debug.debugMode = debugMode;
    }

    /**
     * Logs a debug message if debug mode is enabled.
     *
     * @param message the debug message to log
     */
    public static void info(String message) {
        if (debugMode) {
            System.out.println("DEBUG: " + message);
        }
    }

    /**
     * Logs an warn message if debug mode is enabled.
     *
     * @param message the error message to log
     */
    public static void warn(String message) {
        if (debugMode) {
            System.err.println(YELLOW_TEXT + "WARN: " + message + DEFAULT_TEXT);
        }
    }

    /**
     * Logs an error message if debug mode is enabled.
     *
     * @param message the error message to log
     */
    public static void error(String message) {
        System.err.println(RED_TEXT + "ERROR: " + message + DEFAULT_TEXT);
    }
}
