package com.topcard.business;

import com.topcard.domain.Card;
import com.topcard.domain.Player;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameManagerTest {

    private GameManager gameManager;
    private List<Player> players;

    @Before
    public void setUp() {
        players = generatePlayers();
        gameManager = new GameManager(players);
        gameManager.startGame();
    }

    @Test
    public void testStartGame() {
        for (Player player : players) {
            assertNotNull(player.getHand());
            assertTrue(player.getHand().length > 0);
        }
    }

    @Test
    public void testDisplayWinners() {
        List<Player> winners = players.subList(0, 2);
        gameManager.displayWinners(winners); // Check the console output for the correct winner display
    }

    @Test
    public void testExecuteBettingRound() {
        // initial points for players is 100

        // Execute the betting round with a bet of 10 points
        int betPoints = 10;
        gameManager.executeBettingRound(betPoints);
        // Check that the points are updated correctly

    }

    @Test
    public void testDealCards() {
        gameManager.dealCards();
        for (Player player : players) {
            assertNotNull(player.getHand());
            assertTrue(player.getHand().length > 0);
        }
    }

    @Test
    public void testDetermineWinner() {
        List<Player> winners = gameManager.determineWinner();
        assertNotNull(winners);
        assertFalse(winners.isEmpty());
    }

    @Test
    public void testBetAndUpdatePlayerPoints() {
        int points = 10;
        for (Player player : players) {
            System.out.println(player);
        }
        List<Player> updatedPlayers = gameManager.executeBettingRound(points);
        assertNotNull(updatedPlayers);
        assertEquals(players.size(), updatedPlayers.size());
    }

    @Test
    public void testUpdateProfile() {
        Player player = players.get(0);
        player.setFirstName("SuperMickey");
        gameManager.updateProfile(player);
        Player updatedPlayer = players.get(0);
        assertEquals("SuperMickey", updatedPlayer.getFirstName());
    }

    @Test
    public void testUpdateProfiles() {
        players.get(0).setFirstName("SuperMickey");
        players.get(1).setFirstName("SuperDonald");
        gameManager.updateProfiles(players);
        assertEquals("SuperMickey", players.get(0).getFirstName());
        assertEquals("SuperDonald", players.get(1).getFirstName());
    }

    @Test
    public void testGetHands() {
        List<Card[]> hands = gameManager.getHands();
        assertNotNull(hands);
        assertEquals(players.size(), hands.size());
    }

    @Test
    public void testShowHands() {
        gameManager.showHands();  // Check the console output for the correct hand display
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
}
