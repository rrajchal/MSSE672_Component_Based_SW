package com.topcard.app;

import com.topcard.debug.Debug;
import com.topcard.exceptions.TopCardException;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.InternalFrame;
import com.topcard.presentation.view.LoginView;
import com.topcard.presentation.controller.LoginController;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * This is the main entry point of the application.
 * The MainApp class initializes the application and sets up the main login frame.
 * It also configures the debug mode based on the settings in the config.properties file.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 12/13/2024
 * Subject: MSSE 670 Object Oriented Software Construction
 * </p>
 */
public class MainApp {

    private static final JDesktopPane desktopPane = new JDesktopPane(); // Main desktop pane

    /**
     * The main method serves as the entry point of the application.
     * It initializes the login frame and sets the debug mode based on the configuration.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Copies config file and Set debug mode based on configuration file
        setDebugModeFromConfig();

        // Copies Data file to local
        createDataDirAndFile();

        // Creates Desktop Pane and Menus
        createDesktopPaneAndMenu();
    }

    /**
     * Creates the main application frame, desktop pane, and menu bar.
     * Sets up the File, Debug, and About menus with their respective menu items.
     * Adds action listeners to the menu items and initializes the login view.
     */
    private static void createDesktopPaneAndMenu() {
        // Create the main application frame
        JFrame frame = new JFrame("TopCard Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center the frame on the monitor

        // Create and add a desktop pane to the main frame
        frame.add(desktopPane);
        frame.setVisible(true); // Make the main frame visible

        // Create and set up the menu bar
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // Create the File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Create the Debug menu
        JMenu debugMenu = new JMenu("Debug");
        menuBar.add(debugMenu);

        // Create the About menu
        JMenu aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);

        // Create the menu items
        JMenuItem loginMenuItem = new JMenuItem("Login");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        JMenuItem debugTrueMenuItem = new JMenuItem("Set to True");
        JMenuItem debugFalseMenuItem = new JMenuItem("Set to False");
        JMenuItem aboutGameMenuItem = new JMenuItem("About Game");

        // Add menu items to the menu bar
        fileMenu.add(loginMenuItem);
        fileMenu.add(exitMenuItem);

        // Add menu items to the Debug menu
        debugMenu.add(debugTrueMenuItem);
        debugMenu.add(debugFalseMenuItem);

        // Add menu items to the About menu
        aboutMenu.add(aboutGameMenuItem);

        // Add action listeners to the menu items
        loginMenuItem.addActionListener(e -> openLogin());
        exitMenuItem.addActionListener(e -> System.exit(0));
        debugTrueMenuItem.addActionListener(e -> Debug.setDebugMode(true));
        debugFalseMenuItem.addActionListener(e -> Debug.setDebugMode(false));
        aboutGameMenuItem.addActionListener(e -> showAboutGame());

        // Initialize and add the login view as an internal frame
        LoginView loginView = new LoginView();
        new LoginController(loginView, desktopPane);
        InternalFrame.addInternalFrame(desktopPane, "Login", loginView.getLoginPanel(), 400, 300, false);
    }

    /**
     * Opens the login view as an internal frame if not already opened.
     */
    private static void openLogin() {
        // Check if login view is already open
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame.getTitle().equals("Login")) {
                frame.toFront();
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    Debug.error("Error setting selected frame: " + e.getMessage());
                }
                return;
            }
        }

        // Initialize and add the login view as an internal frame
        LoginView loginView = new LoginView();
        new LoginController(loginView, desktopPane);
        InternalFrame.addInternalFrame(desktopPane, "Login", loginView.getLoginPanel(), 400, 300, false);
    }

    /**
     * Sets the debug mode based on the value in the config.properties file.
     * Set debug.mode=true for displaying debug message
     * Set debug.mode=false for not displaying debug message
     * <p>
     * This method reads the debug mode setting from the config.properties file and
     * configures the Debug class accordingly. If the properties file is not found or
     * an error occurs while reading the file, the debug mode is not set.
     * </p>
     */
    private static void setDebugModeFromConfig() {
        ensureConfigFileExists();
        Properties properties = new Properties();
        Path configFilePath = Paths.get("config", "config.properties");

        try (InputStream input = Files.newInputStream(configFilePath)) {
            // Load the properties file
            properties.load(input);

            // Set debug mode based on the property
            boolean debugMode = Boolean.parseBoolean(properties.getProperty("debug.mode"));
            Debug.setDebugMode(debugMode);
        } catch (IOException ex) {
            Debug.setDebugMode(true);
            Debug.error("Error reading config.properties: " + ex.toString());
            Debug.setDebugMode(false);
        }
    }

    /**
     * Ensures that the config directory and config.properties file exist.
     * If they do not exist, creates the directory and copies the config.properties file.
     */
    private static void ensureConfigFileExists() {
        Path configDir = Paths.get("config");
        Path configFilePath = configDir.resolve("config.properties");

        try {
            // Check if the config directory exists, if not create it
            if (Files.notExists(configDir)) {
                Files.createDirectory(configDir);
            }

            // Check if the config.properties file exists, if not copy it from the classpath
            if (Files.notExists(configFilePath)) {
                try (InputStream input = MainApp.class.getClassLoader().getResourceAsStream("config.properties")) {
                    if (input == null) {
                        Debug.error("Sorry, unable to find config.properties");
                        return;
                    }
                    Files.copy(input, configFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException ex) {
            Debug.error("Error creating config directory or copying config.properties: " + ex.toString());
        }
    }

    /**
     * Ensures that the data directory and file exist.
     * If the file does not exist, it creates the necessary directories and the file.
     *
     * @throws TopCardException if an I/O error occurs while creating the file or directories.
     */
    public static void createDataDirAndFile() {
        Properties properties = new Properties();
        Path configFilePath = Paths.get("config", "config.properties");
        try (InputStream input = Files.newInputStream(configFilePath)) {
            // Load the properties file
            properties.load(input);
            // Get the file path from properties
            String filePath = properties.getProperty("FILE_PATH");
            Path dataFilePath = Paths.get(filePath);

            // Check if the data file exists, if not copy it from the classpath
            if (Files.notExists(dataFilePath)) {
                try (InputStream dataInput = MainApp.class.getClassLoader().getResourceAsStream(filePath)) {
                    if (dataInput == null) {
                        Debug.error("Unable to find data/players.csv");
                        return;
                    }
                    Files.createDirectories(dataFilePath.getParent());
                    Files.copy(dataInput, dataFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException ex) {
            throw new TopCardException("Error reading config.properties or copying data file: " + ex);
        }
    }

    /**
     * Displays the About Game information in a message dialog.
     * This information includes an overview of the game rules, points calculation,
     * and player registration, authentication, profile viewing, and updating.
     */
    private static void showAboutGame() {
        JEditorPane editorPane = new JEditorPane("text/html", Constants.ABOUT_GAME_INFO);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 500));

        JOptionPane.showMessageDialog(null, scrollPane, Constants.ABOUT_GAME, JOptionPane.INFORMATION_MESSAGE);
    }
}


