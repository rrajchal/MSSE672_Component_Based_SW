package com.topcard.domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;

public class GameTest {

    private Game game;
    private Player player1;
    private Player player2;
    private Player player3;

    @Before
    public void setUp() {
        player1 = new Player("username1", "password", "firstName1", "lastName1", LocalDate.of(2000, 1, 1));
        player2 = new Player("username2", "password", "firstName2", "lastName2", LocalDate.of(2000, 2, 1));
        player3 = new Player("username3", "password", "firstName3", "lastName3", LocalDate.of(2000, 3, 1));
        List<Player> players = Arrays.asList(player1, player2, player3);
        game = new Game(players);
    }

    @Test
    public void testStartGame() {
        game.startGame();
        assertNotNull(player1.getHand());
        assertNotNull(player2.getHand());
        assertNotNull(player3.getHand());
        assertNotEquals(0, player1.getHand().length);
        assertNotEquals(0, player2.getHand().length);
        assertNotEquals(0, player3.getHand().length);
    }

    @Test
    public void testPlayCompleteGame() {
        game.startGame();
        assertNotNull(player1.getHand());
        assertNotNull(player2.getHand());
        assertNotNull(player3.getHand());
        assertNotEquals(0, player1.getHand().length);
        assertNotEquals(0, player2.getHand().length);
        assertNotEquals(0, player3.getHand().length);
    }

    @Test
    public void testDetermineWinner() {
        game.startGame();
        player1.drawCard(new Deck());
        player2.drawCard(new Deck());
        player3.drawCard(new Deck());

        List<Player> winners = game.determineWinner();
        assertFalse(winners.isEmpty());
    }

    @Test
    public void testDetermineWinnerByCardRankings() {
        player1.setHand(new Card[] { new Card(Card.Suit.SPADES, Card.Rank.KING) });
        player2.setHand(new Card[] { new Card(Card.Suit.SPADES, Card.Rank.QUEEN) });
        player3.setHand(new Card[] { new Card(Card.Suit.SPADES, Card.Rank.JACK) });

        List<Player> tiedPlayers = Arrays.asList(player1, player2, player3);
        List<Player> winners = game.determineWinnerByCardRankings(tiedPlayers);
        assertEquals(1, winners.size());
        assertEquals(player1, winners.get(0));
    }
}
