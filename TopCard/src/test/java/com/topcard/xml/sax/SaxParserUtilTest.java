package com.topcard.xml.sax;

import com.topcard.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaxParserUtilTest {

    private SaxParserUtil saxParserUtil;

    @BeforeEach
    void setUp() {
        saxParserUtil = new SaxParserUtil();
    }

    private InputStream getXmlFileInputStream(String fileName) throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        assertNotNull(resource, "Test file not found: " + fileName);
        File file = new File(resource.getFile());
        return new FileInputStream(file);
    }

    @Test
    void testSaxParseXmlFile() throws Exception {
        // xml file
        String TEST_XML_FILE_NAME = "players_test.xml";
        try (InputStream is = getXmlFileInputStream(TEST_XML_FILE_NAME)) {
            List<Player> players = saxParserUtil.parsePlayers(is);

            assertNotNull(players);
            assertEquals(3, players.size(), "Expected 3 players (Mickey, Minnie, Donald) from the XML file.");

            Player mickey = players.get(0);
            assertEquals(0, mickey.getPlayerId());
            assertEquals("mickey_mouse", mickey.getUsername());
            assertEquals("password", mickey.getPassword());
            assertEquals("Mickey", mickey.getFirstName());
            assertEquals("Mouse", mickey.getLastName());
            assertEquals(LocalDate.of(1928, 11, 18), mickey.getDateOfBirth());
            assertEquals(999, mickey.getPoints());
            assertTrue(mickey.isAdmin());

            Player minnie = players.get(1);
            assertEquals(0, minnie.getPlayerId());
            assertEquals("minnie_mouse", minnie.getUsername());
            assertEquals("password", minnie.getPassword());
            assertEquals("Minnie", minnie.getFirstName());
            assertEquals("Mouse", minnie.getLastName());
            assertEquals(LocalDate.of(1928, 11, 18), minnie.getDateOfBirth());
            assertEquals(950, minnie.getPoints());
            assertFalse(minnie.isAdmin());

            Player donald = players.get(2);
            assertEquals(0, donald.getPlayerId());
            assertEquals("donald_duck", donald.getUsername());
            assertEquals("password", donald.getPassword());
            assertEquals("Donald", donald.getFirstName());
            assertEquals("Duck", donald.getLastName());
            assertEquals(LocalDate.of(1934, 6, 9), donald.getDateOfBirth());
            assertEquals(700, donald.getPoints());
            assertFalse(donald.isAdmin());
        }
    }

    @Test
    void testParseEmptyPlayersXmlFile() throws Exception {
        // Define an empty XML file
        String EMPTY_PLAYERS_XML_FILE_NAME = "empty_players.xml";
        try (InputStream is = getXmlFileInputStream(EMPTY_PLAYERS_XML_FILE_NAME)) {
            List<Player> players = saxParserUtil.parsePlayers(is);

            assertNotNull(players);
            assertTrue(players.isEmpty(), "Expected an empty list of players from the empty XML file.");
        }
    }
}