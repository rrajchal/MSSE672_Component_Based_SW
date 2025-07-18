package com.topcard.xml.sax;

import com.topcard.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerSaxHandlerTest {

    private PlayerSaxHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PlayerSaxHandler();
    }

    // A helper to create Attributes for elements
    private Attributes createAttributes(String... nameValuePairs) {
        AttributesImpl attrs = new AttributesImpl();
        for (int i = 0; i < nameValuePairs.length; i += 2) {
            attrs.addAttribute("", "", nameValuePairs[i], "CDATA", nameValuePairs[i+1]);
        }
        return attrs;
    }

    // A helper to simulate characters event
    private void characters(String text) throws SAXException {
        char[] chars = text.toCharArray();
        handler.characters(chars, 0, chars.length);
    }

    // A helper to simulate start element with optional attributes
    private void startElement(String qName, Attributes atts) throws SAXException {
        handler.startElement("", "", qName, atts);
    }

    // A helper to simulate end element with optional attributes
    private void endElement(String qName) throws SAXException {
        handler.endElement("", "", qName);
    }

    @Test
    void testSinglePlayerCompleteData() throws SAXException {
        // Simulate a complete XML snippet for one player
        handler.startDocument();
        startElement("Players", createAttributes());
        startElement("Player", createAttributes("id", "10"));
        startElement("username", createAttributes()); characters("testuser"); endElement("username");
        startElement("password", createAttributes()); characters("pass123"); endElement("password");
        startElement("firstName", createAttributes()); characters("Mickey"); endElement("firstName");
        startElement("lastName", createAttributes()); characters("Mouse"); endElement("lastName");
        startElement("dateOfBirth", createAttributes()); characters("1990-01-15"); endElement("dateOfBirth");
        startElement("points", createAttributes()); characters("500"); endElement("points");
        startElement("isAdmin", createAttributes()); characters("true"); endElement("isAdmin");
        endElement("Player");
        endElement("Players");
        handler.endDocument();

        List<Player> players = handler.getPlayers();
        assertNotNull(players);
        assertEquals(1, players.size());

        Player player = players.get(0);
        assertEquals(0, player.getPlayerId());  // Ignores ID
        assertEquals("testuser", player.getUsername());
        assertEquals("pass123", player.getPassword());
        assertEquals("Mickey", player.getFirstName());
        assertEquals("Mouse", player.getLastName());
        assertEquals(LocalDate.of(1990, 1, 15), player.getDateOfBirth());
        assertEquals(500, player.getPoints());
        assertTrue(player.isAdmin());
    }

    @Test
    void testMissingOptionalFields() throws SAXException {
        handler.startDocument();
        startElement("Players", createAttributes());
        startElement("Player", createAttributes()); // No ID attribute
        startElement("username", createAttributes()); characters("partial_user"); endElement("username");
        startElement("firstName", createAttributes()); characters("Partial"); endElement("firstName");
        // Missing password, lastName, dateOfBirth, points, isAdmin
        endElement("Player");
        endElement("Players");
        handler.endDocument();

        List<Player> players = handler.getPlayers();
        assertNotNull(players);
        assertEquals(1, players.size());

        Player player = players.get(0);
        assertEquals(0, player.getPlayerId()); // Default int value
        assertEquals("partial_user", player.getUsername());
        assertNull(player.getPassword()); // Default null for String
        assertEquals("Partial", player.getFirstName());
        assertNull(player.getLastName());
        assertNull(player.getDateOfBirth());
        assertEquals(0, player.getPoints()); // Default int value
        assertFalse(player.isAdmin());     // Default boolean value
    }

    @Test
    void testInvalidDataTypes() throws SAXException {
        handler.startDocument();
        startElement("Players", createAttributes());
        startElement("Player", createAttributes("id", "invalid_id")); // Invalid ID
        startElement("username", createAttributes()); characters("bad_data_user"); endElement("username");
        startElement("dateOfBirth", createAttributes()); characters("not-a-date"); endElement("dateOfBirth"); // Invalid date
        startElement("points", createAttributes()); characters("abc"); endElement("points"); // Invalid points
        startElement("isAdmin", createAttributes()); characters("maybe"); endElement("isAdmin"); // Invalid boolean
        endElement("Player");
        endElement("Players");
        handler.endDocument();

        List<Player> players = handler.getPlayers();
        assertNotNull(players);
        assertEquals(1, players.size());

        Player player = players.get(0);
        assertEquals(0, player.getPlayerId()); // Should be default 0
        assertEquals("bad_data_user", player.getUsername());
        assertNull(player.getDateOfBirth()); // Should be null
        assertEquals(0, player.getPoints()); // Should be default 0
        assertFalse(player.isAdmin());     // Should be default false
    }

    @Test
    void testMultiplePlayersInSequence() throws SAXException {
        handler.startDocument();
        startElement("Players", createAttributes());

        startElement("Player", createAttributes("id", "1"));
        startElement("username", createAttributes()); characters("userA"); endElement("username");
        endElement("Player");

        startElement("Player", createAttributes("id", "2"));
        startElement("username", createAttributes()); characters("userB"); endElement("username");
        endElement("Player");

        endElement("Players");
        handler.endDocument();

        List<Player> players = handler.getPlayers();
        assertNotNull(players);
        assertEquals(2, players.size());
        assertEquals(0, players.get(0).getPlayerId());  // Ignores ID
        assertEquals("userA", players.get(0).getUsername());
        assertEquals("userB", players.get(1).getUsername());
    }
}