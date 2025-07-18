package com.topcard.xml.dom;

import com.topcard.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DomParserUtilTest {

    private DomParserUtil domParserUtil;

    @BeforeEach
    void setUp() {
        domParserUtil = new DomParserUtil();
    }

    private File getXmlFile(String fileName) throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        assertNotNull(resource, "Test file not found: " + fileName);
        return new File(resource.getFile());
    }

    @Test
    void testDomParseXmlFile() throws Exception {
        // non-empty xml file
        File file = getXmlFile("players_test.xml");

        List<Player> players = domParserUtil.parsePlayers(file);

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

    @Test
    void testParseEmptyPlayersXmlFile() throws Exception {
        // Define an empty XML file
        File file = getXmlFile("empty_players.xml");
        List<Player> players = domParserUtil.parsePlayers(file);
        assertNotNull(players);
        assertTrue(players.isEmpty(), "Expected an empty list of players from the empty XML file.");

    }
}