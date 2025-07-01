package com.topcard.service;

import com.topcard.domain.Player;
import com.topcard.domain.PlayerTest;
import com.topcard.service.player.PlayerService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

    private PlayerService testPlayerService;

    @Before
    public void setUp() {
        testPlayerService = new PlayerService();
        deleteAllPlayersData();  // clean all data before starting each test
    }

    @Test
    public void testAddPlayer() {
        Player player = new Player("Mickey", "mouse123", "Mickey", "Mouse", LocalDate.of(1928, 11, 18));
        testPlayerService.addPlayer(player);
        List<Player> allPlayers = testPlayerService.getAllPlayers();
        assertEquals(1, allPlayers.size());
    }

    @Test
    public void testDeleteAllPlayersData() {
        // Call the private method using reflection
        try {
            Method method = PlayerService.class.getDeclaredMethod("deleteAllPlayersData");
            method.setAccessible(true);
            method.invoke(testPlayerService);

            // Check if all data has been deleted
            List<Player> allPlayers = testPlayerService.getAllPlayers();
            assertTrue(allPlayers.isEmpty());
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testRemovePlayer() {
        Player player = new Player("Minnie", "mouse123", "Minnie", "Mouse", LocalDate.of(1928, 11, 18));
        testPlayerService.addPlayer(player);
        int playerId = player.getPlayerId();

        testPlayerService.removePlayer(playerId);
        Player removedPlayer = testPlayerService.getPlayerById(playerId);
        assertNull(removedPlayer);
    }


    @Test
    public void testGetPlayerById() {
        Player player = new Player("donald", "duck123", "Donald", "Duck", LocalDate.of(1934, 6, 9));
        testPlayerService.addPlayer(player);
        int playerId = player.getPlayerId();
        Player noPlayer = testPlayerService.getPlayerById(9999);
        Player fetchedPlayer = testPlayerService.getPlayerById(playerId);
        assertEquals(player.getUsername(), fetchedPlayer.getUsername());
        assertEquals(player.getFirstName(), fetchedPlayer.getFirstName());
        assertNull(noPlayer);
    }

    @Test
    public void testGetPlayerByUsername() {
        Player player = new Player("donald", "duck123", "Donald", "Duck", LocalDate.of(1934, 6, 9));
        testPlayerService.addPlayer(player);
        Player noPlayer = testPlayerService.getPlayerByUsername("panda");
        Player fetchedPlayer = testPlayerService.getPlayerByUsername("donald");
        assertEquals(fetchedPlayer, player);

        assertNull(noPlayer);

    }

    @Test
    public void testGetAllPlayers() {
        createSamplePlayerData();
        List<Player> playerList = testPlayerService.getAllPlayers();
        assertEquals(5, playerList.size());
    }

    void createSamplePlayerData() {
        Player mickey = new Player("mickey", "mouse123", "Mickey", "Mouse", LocalDate.of(1928, 11, 18));
        Player minnie = new Player("minnie", "mouse123", "Minnie", "Mouse", LocalDate.of(1928, 11, 18));
        Player donald = new Player("donald", "duck123", "Donald", "Duck", LocalDate.of(1934, 6, 9));
        Player goofy = new Player("Goofy", "goofy123", "Goofy", "Goof", LocalDate.of(1932, 5, 25));
        Player daisy = new Player("Daisy", "duck123", "Daisy", "Duck", LocalDate.of(1940, 1, 1));
        testPlayerService.addPlayer(mickey);
        testPlayerService.addPlayer(minnie);
        testPlayerService.addPlayer(donald);
        testPlayerService.addPlayer(goofy);
        testPlayerService.addPlayer(daisy);
    }

    void deleteAllPlayersData() {
        // Call the private method using reflection
        try {
            Method method = PlayerService.class.getDeclaredMethod("deleteAllPlayersData");
            method.setAccessible(true);
            method.invoke(testPlayerService);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testChangePoints() {
        Player player = new Player("Goofy", "goofy123", "Goofy", "Goof", LocalDate.of(1932, 5, 25));
        testPlayerService.addPlayer(player);
        int playerId = player.getPlayerId();

        testPlayerService.changePoints(playerId, 50);

        Player updatedPlayer = testPlayerService.getPlayerById(playerId);
        assertEquals(50, updatedPlayer.getPoints());
    }

    @Test
    public void testIsPlayerAdmin() {
        Player player = new Player("Daisy", "duck123", "Daisy", "Duck", LocalDate.of(1940, 1, 1));
        testPlayerService.addPlayer(player); // by default admin is false
        int playerId = player.getPlayerId();

        assertFalse(testPlayerService.isPlayerAdmin(playerId));
        testPlayerService.makePlayerAdmin(playerId); // make player to admin
        assertTrue(testPlayerService.isPlayerAdmin(playerId));
    }

    @Test
    public void testUpdateProfile() {
        Player player = new Player("Pluto", "dog123", "Pluto", "Dog", LocalDate.of(1930, 9, 1));
        testPlayerService.addPlayer(player);
        int playerId = player.getPlayerId();

        testPlayerService.updateProfile(playerId, "NewPluto", "NewDog", LocalDate.of(1930, 9, 5));

        Player updatedPlayer = testPlayerService.getPlayerById(playerId);
        assertEquals("NewPluto", updatedPlayer.getFirstName());
        assertEquals("NewDog", updatedPlayer.getLastName());
        assertEquals(LocalDate.of(1930, 9, 5), updatedPlayer.getDateOfBirth());
    }

    @Test
    public void testCreatePlayers() {
        PlayerTest playerTest = new PlayerTest();
        List<Player> players = playerTest.generatePlayers();
        for (Player player : players) {
            testPlayerService.addPlayer(player);
        }
    }

    @Test
    public void testUpdateData() {
        PlayerTest playerTest = new PlayerTest();
        List<Player> players = playerTest.generatePlayers();
        testPlayerService.addPlayers(players);
        for (Player player : players) {
            player.setPoints(99);
        }
        testPlayerService.updateProfiles(players);

        for (Player player : players) {
            // Also, you can see the data changed in players.csv
            assertEquals(99, testPlayerService.retrievePointForPlayer(player.getPlayerId()));
        }
    }

    @Test
    public void testEncryption() {
        createSamplePlayerData();
        Player retrievedPlayer = testPlayerService.getPlayerByUsername("mickey");
        assertTrue(testPlayerService.verifyPassword("mouse123", retrievedPlayer.getPassword()));
        assertNotEquals("mouse123", retrievedPlayer.getPassword());
    }

}
