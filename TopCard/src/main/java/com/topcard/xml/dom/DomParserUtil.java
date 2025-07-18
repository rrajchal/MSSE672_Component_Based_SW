package com.topcard.xml.dom;

import com.topcard.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to parse XML files using the DOM API.
 * It builds a list of Player objects from an XML file path.
 */
public class DomParserUtil {

    private static final Logger logger = LogManager.getLogger(DomParserUtil.class);

    /**
     * Parses an XML file containing player data and returns a list of Player objects.
     *
     * @param xmlFile The XML file to parse.
     * @return A List of Player objects parsed from the XML.
     * @throws Exception If an error occurs during parsing (e.g., file not found, XML well-formedness issues).
     */
    public List<Player> parsePlayers(File xmlFile) throws Exception {
        List<Player> players = new ArrayList<>();

        if (!xmlFile.exists() || !xmlFile.isFile() || !xmlFile.canRead()) {
            String errorMessage = "XML file not found, is not a file, or is unreadable: " + xmlFile.getAbsolutePath();
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile); // Parse the file into a Document object

        doc.getDocumentElement().normalize(); // Puts all text nodes in "normal" form

        logger.info("DOM parsing started for file: " + xmlFile.getAbsolutePath());
        // Get all <Player> elements
        NodeList playerNodes = doc.getElementsByTagName("Player");
        for (int i = 0; i < playerNodes.getLength(); i++) {
            Node node = playerNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element playerElement = (Element) node;
                Player player = new Player();
                // Get ID attribute (if present) - will be ignored by PlayerManager for auto-increment
                String idStr = playerElement.getAttribute("id");
                if (!idStr.isEmpty()) {
                    try {
                        // player.setPlayerId(Integer.parseInt(idStr));
                        logger.warn("Parsed Player ID from XML (will be ignored by auto-increment): " + idStr);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid Player ID format in XML for id: " + idStr + ". Skipping ID for this player.", e);
                    }
                }

                // Extract text content for each child element
                player.setUsername(getTagValue("username", playerElement));
                player.setPassword(getTagValue("password", playerElement)); // This will be the HASHED password from XML
                player.setFirstName(getTagValue("firstName", playerElement));
                player.setLastName(getTagValue("lastName", playerElement));
                String dateOfBirthStr = getTagValue("dateOfBirth", playerElement);
                if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                    try {
                        player.setDateOfBirth(LocalDate.parse(dateOfBirthStr)); // Assumes YYYY-MM-DD
                    } catch (DateTimeParseException e) {
                        logger.warn("Invalid date of birth format for player " + player.getUsername() + ": " + dateOfBirthStr + ". Setting to null.", e);
                        player.setDateOfBirth(null);
                    }
                }

                String pointsStr = getTagValue("points", playerElement);
                if (pointsStr != null && !pointsStr.isEmpty()) {
                    try {
                        player.setPoints(Integer.parseInt(pointsStr));
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid points format for player " + player.getUsername() + ": " + pointsStr + ". Setting to default (0).", e);
                        player.setPoints(0);
                    }
                }

                String isAdminStr = getTagValue("isAdmin", playerElement);
                if (isAdminStr != null && !isAdminStr.isEmpty()) {
                    player.setAdmin(Boolean.parseBoolean(isAdminStr));
                } else {
                    player.setAdmin(false); // Default
                }

                players.add(player);
            }
        }
        logger.info("DOM parsing finished. Found " + players.size() + " players.");
        return players;
    }

    /**
     * Helper method to get the text content of a child element by tag name.
     *
     * @param tagName The name of the child tag.
     * @param element The parent Element to search within.
     * @return The text content of the first child element with the given tag name, or null if not found.
     */
    private String getTagValue(String tagName, Element element) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            NodeList subList = nodeList.item(0).getChildNodes();
            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return null;
    }
}