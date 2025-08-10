package com.topcard.xml.sax;

import com.topcard.domain.Player;
import com.topcard.exceptions.TopCardException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility class to parse XML files using the SAX API.
 */
public class SaxParserUtil {
    private static final Logger logger = LogManager.getLogger(SaxParserUtil.class);

    /**
     * Parses an XML input stream containing player data and returns a list of Player objects.
     *
     * @param xmlInputStream The InputStream of the XML file.
     * @return A List of Player objects, or null if an error occurs during parsing.
     */
    public List<Player> parsePlayers(InputStream xmlInputStream) {
        if (xmlInputStream == null) {
            System.err.println("Error: InputStream for XML parsing is null.");
            return null;
        }

        try {
            // Get a SAXParserFactory instance
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // Get a SAXParser instance
            SAXParser saxParser = factory.newSAXParser();
            // Create an instance of your custom SAX handler
            PlayerSaxHandler handler = new PlayerSaxHandler();

            // Parse the XML input stream using the handler
            saxParser.parse(xmlInputStream, handler);

            // Return the list of players collected by the handler
            return handler.getPlayers();
        } catch (ParserConfigurationException e) {
            logger.error("Parser configuration error: " + e.getMessage());
        } catch (SAXException e) {
            logger.error("SAX parsing error: " + e.getMessage());
        } catch (IOException e) {
            logger.error("I/O error during XML parsing: " + e.getMessage());
        } catch (Exception e) { // Catch any other unexpected exceptions
            logger.error("An unexpected error occurred during SAX parsing: " + e.getMessage());
        }
        return null; // Return null if any error occurred
    }

    public Map<String, String> parseServiceMappings(InputStream xmlInputStream) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            ServiceMappingSaxHandler handler = new ServiceMappingSaxHandler();
            saxParser.parse(xmlInputStream, handler);
            return handler.getServiceMappings();
        } catch (Exception e) {
            throw new TopCardException("Error parsing services.xml", e);
        }
    }
}