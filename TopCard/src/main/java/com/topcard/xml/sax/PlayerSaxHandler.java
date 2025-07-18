package com.topcard.xml.sax;

import com.topcard.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * SAX handler for parsing Player XML data.
 * It builds a list of Player objects from an XML stream.
 */
public class PlayerSaxHandler extends DefaultHandler {

    private static final Logger logger = LogManager.getLogger(PlayerSaxHandler.class);

    private List<Player> players;
    private Player currentPlayer;
    private StringBuilder characters; // Buffer to hold character data between XML tags
    private boolean isParsingPlayersRoot = false; // Flag to ensure we are inside the <Players> root

    /**
     * Returns the list of Player objects parsed from the XML.
     * @return A list of Player objects.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Called at the beginning of the XML document.
     * Initializes the list of players and the character buffer.
     */
    @Override
    public void startDocument() throws SAXException {
        players = new ArrayList<>();
        characters = new StringBuilder();
        logger.info("SAX parsing started.");
    }

    /**
     * Called when the parser encounters the start of an XML element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI.
     * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        characters.setLength(0); // Clear the buffer for new element content

        if (qName.equalsIgnoreCase("Players")) {
            isParsingPlayersRoot = true; // Mark that we are inside the main Players container
        } else if (qName.equalsIgnoreCase("Player") && isParsingPlayersRoot) {
            currentPlayer = new Player(); // Start a new Player object
            // Parse 'id' attribute from <Player> tag
            String idStr = attributes.getValue("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try {
                    //currentPlayer.setPlayerId(Integer.parseInt(idStr));
                    // Ignore Player ID if xml file contains it
                    logger.warn("Player ID '{}' found in XML will be ignored as IDs are auto-generated.", idStr);
                } catch (NumberFormatException e) {
                    logger.error("Warning: Invalid Player ID format for id: " + idStr + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Called when the parser encounters character data (text content) within an element.
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(ch, start, length); // Append characters to the buffer
    }

    /**
     * Called when the parser encounters the end of an XML element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI.
     * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are not available.
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("Player") && isParsingPlayersRoot) {
            // Add the completed Player object to the list
            if (currentPlayer != null) {
                players.add(currentPlayer);
                currentPlayer = null; // Reset for the next player
            }
        } else if (qName.equalsIgnoreCase("Players")) {
            isParsingPlayersRoot = false; // Exited the main Players container
        } else if (currentPlayer != null) { // We are inside a <Player> element and processing its sub-tags
            String data = characters.toString().trim(); // Get the accumulated text data
            switch (qName.toLowerCase()) {
                case "username":
                    currentPlayer.setUsername(data);
                    break;
                case "password":
                    currentPlayer.setPassword(data); // Remember this is the HASHED password from XML
                    break;
                case "firstname":
                    currentPlayer.setFirstName(data);
                    break;
                case "lastname":
                    currentPlayer.setLastName(data);
                    break;
                case "dateofbirth":
                    try {
                        // Assuming YYYY-MM-DD format as per your Player class LocalDate
                        currentPlayer.setDateOfBirth(LocalDate.parse(data));
                    } catch (DateTimeParseException e) {
                        System.err.println("Warning: Invalid date of birth format for player " + currentPlayer.getUsername() + ": " + data);
                        // Optionally set to null or a default date, or throw SAXException
                    }
                    break;
                case "points":
                    try {
                        currentPlayer.setPoints(Integer.parseInt(data));
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Invalid points format for player " + currentPlayer.getUsername() + ": " + data);
                    }
                    break;
                case "isadmin":
                    currentPlayer.setAdmin(Boolean.parseBoolean(data));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Called at the end of the XML document.
     */
    @Override
    public void endDocument() throws SAXException {
        System.out.println("SAX parsing finished.");
    }
}