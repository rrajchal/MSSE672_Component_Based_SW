package com.topcard.xml;

import com.topcard.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class to write a list of Player objects to an XML file.
 * It includes player ID but excludes password for security reasons.
 */
public class PlayerXmlWriter {

    private static final Logger logger = LogManager.getLogger(PlayerXmlWriter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-DD");

    /**
     * Writes a list of Player objects to an XML file.
     * The XML will include player ID but exclude the password.
     *
     * @param players The list of Player objects to write.
     * @param filePath The absolute path to the output XML file.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws TransformerException If an error occurs during XML transformation.
     */
    public static void writePlayersToXml(List<Player> players, String filePath)
            throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Root element <Players>
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Players");
        doc.appendChild(rootElement);

        for (Player player : players) {
            // Player element <Player id="X">
            Element playerElement = doc.createElement("Player");
            playerElement.setAttribute("id", String.valueOf(player.getPlayerId()));
            rootElement.appendChild(playerElement);

            // <username>
            Element username = doc.createElement("username");
            username.appendChild(doc.createTextNode(player.getUsername()));
            playerElement.appendChild(username);

            // Password is NOT included for security

            // <firstName>
            Element firstName = doc.createElement("firstName");
            firstName.appendChild(doc.createTextNode(player.getFirstName()));
            playerElement.appendChild(firstName);

            // <lastName>
            Element lastName = doc.createElement("lastName");
            lastName.appendChild(doc.createTextNode(player.getLastName()));
            playerElement.appendChild(lastName);

            // <dateOfBirth>
            Element dateOfBirth = doc.createElement("dateOfBirth");
            dateOfBirth.appendChild(doc.createTextNode(player.getDateOfBirth().format(DATE_FORMATTER)));
            playerElement.appendChild(dateOfBirth);

            // <points>
            Element points = doc.createElement("points");
            points.appendChild(doc.createTextNode(String.valueOf(player.getPoints())));
            playerElement.appendChild(points);

            // <isAdmin>
            Element isAdmin = doc.createElement("isAdmin");
            isAdmin.appendChild(doc.createTextNode(String.valueOf(player.isAdmin())));
            playerElement.appendChild(isAdmin);
        }

        // Write the content into XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // For pretty printing
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // Indent by 4 spaces
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // Ensure UTF-8 encoding

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));

        transformer.transform(source, result);
        logger.info("XML file saved successfully to: " + filePath);
    }
}