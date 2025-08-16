package com.topcard.app;

import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.InternalFrame;
import com.topcard.presentation.controller.LoginController;
import com.topcard.presentation.view.LoginView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This is the main entry point of the application.
 * The MainApp class initializes the application and sets up the main login frame.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class MainApp {

    private static final Logger logger = LogManager.getLogger(MainApp.class);

    private static final JDesktopPane desktopPane = new JDesktopPane(); // Main desktop pane

    /**
     * The main method serves as the entry point of the application.
     * It initializes the login frame and sets the debug mode based on the configuration.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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

        debugTrueMenuItem.addActionListener(e -> updateLoggingLevel("DEBUG"));
        debugFalseMenuItem.addActionListener(e -> updateLoggingLevel("ERROR"));

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
                    logger.error("Error setting selected frame: " + e.getMessage());
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

    /**
     * Dynamically updates the root logger level at runtime using Log4j2's internal API.
     *
     * <p>This method updates the in-memory configuration and applies it immediately.
     * It does not rely on modifying the log4j2.properties file.</p>
     *
     * @param level the desired log level (e.g., "DEBUG", "ERROR", "INFO")
     */
    public static void updateLoggingLevel(String level) {
        Path log4jConfigFilePath = Paths.get("config", "log4j2.properties");
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(log4jConfigFilePath.toFile())) {
            props.load(in);
        } catch (IOException e) {
            logger.error("Error loading log4j2.properties for update: " + e.getMessage());
            return;
        }

        // Update the property
        props.setProperty("rootLogger.level", level);
        logger.error("This is the current log level: " + props.getProperty("rootLogger.level"));
        // Inside updateLoggingLevel, after the FileOutputStream block:
        try (FileOutputStream out = new FileOutputStream(log4jConfigFilePath.toFile())) {
            props.store(out, "Updated rootLogger.level to " + level);
            logger.info("Successfully updated log4j2.properties file at: " + log4jConfigFilePath.toAbsolutePath()); // ADD THIS LINE
        } catch (IOException e) {
            logger.error("Error saving log4j2.properties after update: " + e.getMessage());
            return;
        }

        // Reconfigure Log4j2 from the updated file
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        try {
            ctx.setConfigLocation(log4jConfigFilePath.toUri());
            // In some Log4j2 versions, a direct reconfigure call might be needed if setConfigLocation doesn't trigger it
            ctx.reconfigure();
            logger.info("Log4j2 reconfigured from file. Current in-memory logging level for MainApp: " + logger.getLevel());
        } catch (Exception e) {
            logger.error("Error reconfiguring Log4j2 from file: " + e.getMessage());
        }

        // This logger.info will only be visible if the new level is INFO or lower (DEBUG, TRACE)
        // If 'level' is "ERROR", this INFO message won't show.
        logger.info("Attempted to set logging level to: " + level);
    }
}


