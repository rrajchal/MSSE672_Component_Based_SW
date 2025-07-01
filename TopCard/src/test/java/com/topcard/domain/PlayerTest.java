package com.topcard.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.topcard.exceptions.TopCardException;
import org.junit.Test;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerTest {

    @Test
    public void testPlayerConstructor() {
        LocalDate dob = LocalDate.of(1990, 1, 1); // January 1, 1990
        Player player = new Player("", "", "Mickey", "Mouse", dob);

        assertEquals("Mickey", player.getFirstName());
        assertEquals("Mouse", player.getLastName());
        assertEquals(dob, player.getDateOfBirth());
        assertEquals(100, player.getPoints());
        assertFalse(player.isAdmin());
        assertNotNull(player.getHand());
        player.setNumOfCards(3);
        assertNotNull(player.getHand());
        assertEquals(3, player.getHand().length);
    }

    @Test
    public void testSetAdmin() {
        Player player = new Player("", "", "Donald", "Duck", LocalDate.of(1995, 6, 15));
        player.setAdmin(true);
        assertTrue(player.isAdmin());
    }

    @Test
    public void testDrawCards() {
        Player player = new Player("", "", "Michael", "Smith", LocalDate.of(1985, 11, 25));
        Deck deck = new Deck();
        deck.shuffle();
        Card oneCard = player.drawCard(deck);
        Card[] cards = player.drawCards(deck);

        int nonNullCards = 0;
        for (Card card : player.getHand()) {
            if (card != null) {
                nonNullCards++;
            }
        }
        assertTrue(oneCard.getCardValue(oneCard) > 0);
        assertEquals(3, cards.length);
        assertEquals(3, nonNullCards);
    }

    @Test
    public void testGetHandValue() {
        Player player = new Player("", "", "Rajesh", "Rajchal", LocalDate.of(1980, 4, 10));
        Deck deck = new Deck();
        deck.shuffle();

        player.drawCards(deck);
        player.drawCards(deck);
        player.drawCards(deck);

        int handValue = player.getHandValue();
        assertTrue(handValue > 0);
    }

    @Test
    public void testChangePoints() {
        Player player = new Player("", "", "Charlie", "Brown", LocalDate.of(2000, 2, 1));
        player.changePoints(10);
        assertEquals(110, player.getPoints());

        player.changePoints(5);
        assertEquals(115, player.getPoints());
    }

    @Test
    public void testGetAge() {
        LocalDate dob = LocalDate.of(1995, 6, 15); // June 15, 1995
        Player player = new Player("", "", "Dana", "White", dob);
        int age = player.getAge();
        assertTrue(age > 0);
    }

    @Test
    public void testShowHand() {
        Player player = new Player("", "", "Mickey", "Mouse", LocalDate.of(1990, 1, 1));
        Deck deck = new Deck();
        deck.shuffle();

        player.drawCards(deck);
        player.drawCards(deck);
        player.drawCards(deck);

        // Capture the output of showHand()
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent)); //
        //System.out.println(outContent);

        player.showHand();

        // Verify the output is not empty and contains cards
        String output = outContent.toString();
        assertFalse(output.isEmpty());
        assertTrue(output.contains(" OF "));
    }

    @Test
    public void testIsAuthorized() {
        Player player = new Player("", "", "Baby", "King", LocalDate.of(2010, 5, 20)); // Under 18
        assertFalse(player.isAuthorized());

        player = new Player("", "", "Adult", "Queen", LocalDate.of(1990, 3, 10)); // Over 18
        assertTrue(player.isAuthorized());
    }

    @Test
    public void testBet() {
        Player player1 = new Player("username1", "password", "firstName1", "lastName1", LocalDate.of(2000, 1, 1));
        Player player2 = new Player("username2", "password", "firstName2", "lastName2", LocalDate.of(2000, 2, 1));
        Player player3 = new Player("username3", "password", "firstName3", "lastName3", LocalDate.of(2000, 3, 1));
        player1.setPoints(100);
        player2.setPoints(100);
        player3.setPoints(100);

        // Set hands with three cards each
        player1.setHand(new Card[] {
                new Card(Card.Suit.SPADES, Card.Rank.KING),   // Value: 10
                new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),  // Value: 10
                new Card(Card.Suit.DIAMONDS, Card.Rank.JACK)  // Value: 10
        }); // Total hand value: 30

        player2.setHand(new Card[] {
                new Card(Card.Suit.CLUBS, Card.Rank.NINE),    // Value: 9
                new Card(Card.Suit.SPADES, Card.Rank.EIGHT),  // Value: 8
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN)   // Value: 7
        }); // Total hand value: 24

        player3.setHand(new Card[] {
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),  // Value: 6
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),    // Value: 5
                new Card(Card.Suit.SPADES, Card.Rank.FOUR)    // Value: 4
        }); // Total hand value: 15

        List<Player> playerWithNewPoints = player1.updatePoints(10, Arrays.asList(player2, player3));


        assertEquals(120, player1.getPoints()); // Player 1 wins 10 points from both Player 2 and Player 3
        assertEquals(90, player2.getPoints());
        assertEquals(90, player3.getPoints());
    }

    @Test
    public void testDefalutPoint() {
        List<Player> players = generatePlayers();
        assertEquals(100, players.get(0).getPoints());
    }

    @Test
    public void testToString() {
        Player mickey = new Player("mickey_mouse", "disney123", "Mickey", "Mouse", LocalDate.of(1928, 11, 18));
        mickey.setPlayerId(1);
        mickey.setPoints(500);
        mickey.setAdmin(false);
        mickey.setLoggedIn(true);

        String playerString = mickey.toString();

        assertTrue(playerString.contains("playerId=1"), "toString should contain playerId=1");
        assertTrue(playerString.contains("username='mickey_mouse'"), "toString should contain username='mickey_mouse'");
    }

    @Test
    public void testSerialization() {
        List<Player> players = new ArrayList<>();
        Player mickey = new Player("mickey", "password", "Mickey", "Mouse", LocalDate.of(1990, 1, 1));
        Player donald = new Player("donald", "password", "Donald", "Duck", LocalDate.of(1995, 6, 15));
        mickey.setPlayerId(1);
        donald.setPlayerId(2);
        players.add(mickey);
        players.add(donald);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("players.ser"))) {
            oos.writeObject(players);
        } catch (Exception e) {
            fail("Serialization error: " + e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeserialization() {
        File file = new File("players.ser");
        if (!file.exists()) {
            testSerialization();
        }
        List<Player> players = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("players.ser"))) {
            players = (List<Player>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            fail("Deserialization error: " + e.getMessage());
        }
        //players.forEach(System.out::println);
        assertNotNull(players);
        assertEquals(1, players.get(0).getPlayerId());
        assertEquals("Mickey", players.get(0).getFirstName());
    }

    public List<Player> generatePlayers() {
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
