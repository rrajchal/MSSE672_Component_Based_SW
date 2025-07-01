package com.topcard.business;

import com.topcard.domain.Player;
import com.topcard.exceptions.TopCardException;
import com.topcard.service.player.PlayerService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerManagerTest {

    private PlayerManager playerManager;

    @Before
    public void setUp() {
        playerManager = new PlayerManager();
        clearData();
    }

    @Test
    public void testAddPlayer() {
        Player player = new Player("rajesh", "password", "Rajesh", "Lastname", LocalDate.now());
        playerManager.addPlayer(player);
        Player result = playerManager.getPlayerByUsername("rajesh");
        assertNotNull(result);
        assertEquals("rajesh", result.getUsername());
    }

    @Test
    public void testAddPlayers() {
        List<Player> players = generatePlayers();
        playerManager.addPlayers(players);
        assertEquals(4, playerManager.getAllPlayers().size());
    }

    @Test
    public void testRemovePlayer() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        playerManager.removePlayer(player.getPlayerId());
        Player result = playerManager.getPlayerById(player.getPlayerId());
        assertNull(result);
    }

    @Test
    public void testGetPlayerById() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        Player result = playerManager.getPlayerById(player.getPlayerId());
        assertEquals(player, result);
    }

    @Test
    public void testGetPlayerByUserName() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        Player result = playerManager.getPlayerByUsername(player.getUsername());
        assertEquals(player, result);
    }

    @Test
    public void testChangePoints() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        playerManager.changePoints(player.getPlayerId(), 10);
        assertEquals(110, playerManager.retrievePointForPlayer(player.getPlayerId()));
    }

    @Test
    public void testIsPlayerAdmin() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        playerManager.makePlayerAdmin(player.getPlayerId());
        assertTrue(playerManager.isPlayerAdmin(player.getPlayerId()));
    }

    @Test
    public void testMakePlayerAdmin() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        playerManager.makePlayerAdmin(player.getPlayerId());
        assertTrue(playerManager.isPlayerAdmin(player.getPlayerId()));
    }

    @Test
    public void testUpdateProfile() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        player.setFirstName("Johnny");
        playerManager.updateProfile(player);
        Player result = playerManager.getPlayerByUsername(player.getUsername());
        assertEquals("Johnny", result.getFirstName());
    }

    @Test
    public void testUpdateProfileById() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        playerManager.updateProfile(player.getPlayerId(), "Johnny", "Smith", LocalDate.of(1990, 1, 1));
        Player result = playerManager.getPlayerById(player.getPlayerId());
        assertEquals("Johnny", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
    }

    @Test
    public void testUpdateProfiles() {
        List<Player> players = generatePlayers();
        playerManager.addPlayers(players);
        players.get(0).setFirstName("Johnny");
        players.get(1).setFirstName("Janet");
        playerManager.updateProfiles(players);
        assertEquals("Johnny", playerManager.getPlayerByUsername("mickey").getFirstName());
        assertEquals("Janet", playerManager.getPlayerByUsername("donald").getFirstName());
    }

    @Test
    public void testRetrievePointForPlayer() {
        Player player = generatePlayers().get(0);
        playerManager.addPlayer(player);
        playerManager.changePoints(player.getPlayerId(), 100);
        int points = playerManager.retrievePointForPlayer(player.getPlayerId());
        assertEquals(200, points);
    }

    @Test
    public void testGetAllPlayers() {
        List<Player> players = generatePlayers();
        playerManager.addPlayers(players);
        List<Player> result = playerManager.getAllPlayers();
        assertEquals(players, result);
    }

    @Test
    public void testDeleteAllPlayersData() {
        // Use reflection to call the private deleteAllPlayersData() method
        PlayerService playerService = new PlayerService();
        try {
            Method method = PlayerService.class.getDeclaredMethod("deleteAllPlayersData");
            method.setAccessible(true);
            method.invoke(playerService);
        } catch (Exception e) {
            throw new TopCardException("Failed to delete all player data", e);
        }
    }

    private List<Player> generatePlayers() {
        List<Player> players = new ArrayList<>();
        Player mickey = new Player("mickey", "password", "Mickey", "Mouse", LocalDate.of(1990, 1, 1));
        Player donald = new Player("donald", "password", "Donald", "Duck", LocalDate.of(1995, 6, 15));
        Player michael = new Player("michael", "password", "Michael", "Smith", LocalDate.of(1985, 11, 25));
        Player rajesh = new Player("rajesh", "password", "Rajesh", "Rajchal", LocalDate.of(1980, 4, 10));
        players.add(mickey);
        players.add(donald);
        players.add(michael);
        players.add(rajesh);
        return players;
    }

    /**
     * Delete everything before starting every test case
     */
    private void clearData() {
        testDeleteAllPlayersData();
    }
}
