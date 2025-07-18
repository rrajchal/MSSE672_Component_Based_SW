package com.topcard.presentation.controller;

import com.topcard.business.PlayerManager;
import com.topcard.domain.Player;
import com.topcard.presentation.common.Constants;
import com.topcard.presentation.common.Validation;
import com.topcard.presentation.view.AddPlayerView;
import com.topcard.xml.PlayerXmlWriter;
import com.topcard.xml.dom.DomParserUtil;  // If DOM is used
import com.topcard.xml.sax.PlayerSaxHandler;
import com.topcard.xml.sax.SaxParserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.xml.parsers.SAXParserFactory;

/**
 * This class represents the controller for the add player view.
 * It manages the user interactions for adding a new player.
 *
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class AddPlayerController {

    private static final Logger logger = LogManager.getLogger(AddPlayerController.class);

    private final AddPlayerView addPlayerView;
    private final PlayerManager playerManager;

    /**
     * Constructor to initialize the add player controller with the given add player view.
     *
     * @param addPlayerView the add player view
     */
    public AddPlayerController(AddPlayerView addPlayerView) {
        this.addPlayerView = addPlayerView;
        this.playerManager = new PlayerManager();
        initController();
    }

    /**
     * Initializes the controller by setting up the action listeners.
     */
    private void initController() {
        logger.info("Initializing AddPlayer Controller");
        addPlayerView.getAddPlayerButton().addActionListener(e -> handleAddSinglePlayer());     // Listener for add single player
        addPlayerView.getUploadXmlButton().addActionListener(e -> handleUploadXmlPlayers());    // Listener for add players from XML file
        addPlayerView.getDownloadXmlButton().addActionListener(e -> handleDownloadXmlPlayers());// Listener for downloading all data from database
        addPlayerView.getXmlFilePathField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleBrowseXmlFile();
            }
        });
    }

    private void handleBrowseXmlFile() {
        logger.info("Browse XML file button pressed.");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Ensure only files can be selected

        // Show the file chooser dialog, relative to the AddPlayerView window
        int returnValue = fileChooser.showOpenDialog(addPlayerView);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Update the view's XML file path field with the selected file's absolute path
            addPlayerView.getXmlFilePathField().setText(selectedFile.getAbsolutePath());
            logger.info("Selected XML file: " + selectedFile.getAbsolutePath());
        } else {
            logger.info("XML file selection cancelled.");
            addPlayerView.getXmlFilePathField().setText(Constants.CLICK_TO_UPLOAD_XML_FILE);
        }
    }

    /**
     * Handles the add player process when the add player button is clicked.
     * It validates the input fields and adds a new player if all fields are valid.
     */
    private void handleAddSinglePlayer() {
        Object[] textFields = {addPlayerView.getFirstNameField(), addPlayerView.getLastNameField(),
            addPlayerView.getUsernameField(), addPlayerView.getPasswordField(), addPlayerView.getDateOfBirthField()};

        String[] errorMessages = { Constants.FIRST_NAME_CANNOT_HAVE_SPACES, Constants.LAST_NAME_CANNOT_HAVE_SPACES,
                Constants.USERNAME_CANNOT_HAVE_SPACES, Constants.PASSWORD_CANNOT_HAVE_SPACES, Constants.REQUIRED};

        boolean isValid = Validation.validateFields(textFields, errorMessages);

        if (isValid) {
            // Validate date of birth format
            LocalDate dateOfBirth;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                dateOfBirth = LocalDate.parse(addPlayerView.getDateOfBirthField().getText().trim(), formatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(addPlayerView, Constants.INVALID_DATE, Constants.ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add player
            Player newPlayer = new Player(
                    addPlayerView.getUsernameField().getText(),
                    new String(addPlayerView.getPasswordField().getPassword()), // Update the password
                    addPlayerView.getFirstNameField().getText(),
                    addPlayerView.getLastNameField().getText(),
                    dateOfBirth
            );

            boolean playerAdded = playerManager.addPlayer(newPlayer);
            if (playerAdded) {
                JOptionPane.showMessageDialog(addPlayerView, Constants.PLAYER_ADDED, Constants.SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(addPlayerView, Constants.PLAYER_NOT_ADDED, Constants.FAILED, JOptionPane.INFORMATION_MESSAGE);
            }

            addPlayerView.dispose(); // Close the AddPlayerView
        }
    }

    /**
     * Handles the XML file upload process when the "Upload XML Players" button is clicked.
     */
    private void handleUploadXmlPlayers() {
        String filePath = addPlayerView.getXmlFilePathField().getText();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(addPlayerView, "Please select an XML file.", Constants.ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        File xmlFile = new File(filePath);
        if (!xmlFile.exists() || !xmlFile.isFile() || !xmlFile.canRead()) {
            JOptionPane.showMessageDialog(addPlayerView, "Selected file is invalid or unreadable.", Constants.ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Player> playersToAdd = null;

        // Parser: SAX or DOM

        // --- Using SAX Parser ---
        SaxParserUtil saxParser = new SaxParserUtil();
        try (InputStream inputStream = new FileInputStream(xmlFile)) {
            PlayerSaxHandler handler = new PlayerSaxHandler();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            javax.xml.parsers.SAXParser parser = factory.newSAXParser();
            parser.parse(inputStream, handler);
            playersToAdd = handler.getPlayers();
        } catch (Exception e) {
            logger.error("Error parsing XML file with SAX: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(addPlayerView, "Error parsing XML file: " + e.getMessage(), Constants.ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        /*
        // --- OR Using DOM Parser (uncomment and replace SAX block if you prefer DOM) ---
        DomParserUtil domParser = new DomParserUtil();
        try {
            playersToAdd = domParser.parsePlayers(xmlFile);
        } catch (Exception e) {
            logger.error("Error parsing XML file with DOM: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(addPlayerView, "Error parsing XML file: " + e.getMessage(), Constants.ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        */

        if (playersToAdd != null && !playersToAdd.isEmpty()) {
            int addedCount = 0;
            for (Player player : playersToAdd) {
                // Ensure PlayerManager.addPlayer handles password hashing correctly for XML imported players
                boolean playerAdded = playerManager.addPlayer(player);
                if (playerAdded) {
                    addedCount++;
                } else {
                    logger.warn("Failed to add player from XML: " + player.getUsername() + ". Possibly a duplicate username.");
                }
            }

            if (addedCount > 0) {
                JOptionPane.showMessageDialog(addPlayerView, addedCount + " player(s) added successfully from XML!", Constants.SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(addPlayerView, "No new players were added from the XML file.", Constants.FAILED, JOptionPane.WARNING_MESSAGE);
            }
            addPlayerView.dispose(); // Close after batch upload
        } else {
            JOptionPane.showMessageDialog(addPlayerView, "No players found in the XML file or parsing failed.", Constants.FAILED, JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Handles the XML file download process when the "Download All Players as XML" button is clicked.
     */
    private void handleDownloadXmlPlayers() {
        logger.info("Download XML players button pressed.");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Players XML As...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Suggest a default file name
        fileChooser.setSelectedFile(new File("players_data.xml"));

        int userSelection = fileChooser.showSaveDialog(addPlayerView);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Ensure the file has a .xml extension
            if (!fileToSave.getAbsolutePath().endsWith(".xml")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xml");
            }

            try {
                List<Player> allPlayers = playerManager.getAllPlayers(); // Retrieve all players
                if (allPlayers != null && !allPlayers.isEmpty()) {
                    PlayerXmlWriter.writePlayersToXml(allPlayers, fileToSave.getAbsolutePath());
                    JOptionPane.showMessageDialog(addPlayerView, "Players data saved to:\n" + fileToSave.getAbsolutePath(), Constants.SUCCESS, JOptionPane.INFORMATION_MESSAGE);
                    logger.info("Players data successfully downloaded to: " + fileToSave.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(addPlayerView, "No players available to download.", Constants.WARNING, JOptionPane.WARNING_MESSAGE);
                    logger.warn("Attempted to download players, but no players were found.");
                }
            } catch (Exception ex) {
                logger.error("Error saving players to XML file: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(addPlayerView, "Error saving players to XML file: " + ex.getMessage(), Constants.ERROR, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            logger.info("XML file download cancelled by user.");
        }
    }
}
