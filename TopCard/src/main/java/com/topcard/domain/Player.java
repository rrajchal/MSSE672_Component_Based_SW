package com.topcard.domain;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Player class represents a player in the TopCard game.
 * A player has a unique ID, username, password, first name, last name, date of birth, points, admin status, and login status.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Entity // JPA entity class
@Table(name = "players") // Map this entity to the 'players' table
public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = 4L;

    private static final Logger logger = LogManager.getLogger(Player.class);

    @Id // playerId as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment for the ID
    @Column(name = "player_id") // Map to the 'player_id' column
    private int playerId;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255) // Encrypted password hash, suitable length
    private String password;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "date_of_birth") // Hibernate can map LocalDate directly to DATE type
    private LocalDate dateOfBirth;

    @Column(name = "points")
    private int points;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Transient // Transient field will NOT be persisted to the database
    private boolean isLoggedIn;

    @Transient
    private static final int AGE_THRESHOLD = 18; // Eligible age to play this game

    @Transient
    private int numOfCards = 3;  // 3 by default for this TopCard card game. However, for scalability we can set differently

    @Transient
    private Card[] hand;

    /**
     * Required no-argument constructor for Hibernate.
     */
    public Player() {
        // Default constructor required by JPA/Hibernate
    }

    /**
     * Constructs a Player with the specified details, excluding admin status.
     * isAdmin is set to false by default.
     *
     * @param username    the player's username
     * @param password    the player's password
     * @param firstName   the player's first name
     * @param lastName    the player's last name
     * @param dateOfBirth the player's date of birth
     */
    public Player(String username, String password, String firstName, String lastName, LocalDate dateOfBirth) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.points = 100;    // Default when a player is created.
        this.isAdmin = false; // Default to false
        this.isLoggedIn = false;
    }

    // Getters
    public int getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public int getPoints() {
        return points;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public int getNumOfCards() {
        if (this.numOfCards <= 0 || this.numOfCards > Deck.NUM_OF_CARDS_IN_DECK) {
            setNumOfCards(this.numOfCards); // will set to default number of cards.
        }
        return this.numOfCards;
    }

    public Card[] getHand() {
        if (this.hand == null) {
            this.hand = new Card[getNumOfCards()];
            setHand(this.hand);
        }
        return this.hand;
    }

    // Setters
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public void setNumOfCards(int numOfCards) {
        if (numOfCards > 0) {
            this.numOfCards = numOfCards;
        }
    }

    public void setHand(Card[] hand) {
        this.hand = hand;
    }

    /**
     * Changes points based on win (add) or loss (subtract, adds a negative value)
     * Also changes in data in persistence via service layer by calling DAO
     * @param points points to be added
     */
    public void changePoints(int points) {
        this.points += points;
    }

    public boolean checkAdminStatus() {
        return this.isAdmin;
    }

    /**
     * Checks if the player is authorized based on the age threshold.
     *
     * @return true if the player's age is equal to or greater than the age threshold, false otherwise.
     */
    public boolean isAuthorized() {
        return LocalDate.now().minusYears(AGE_THRESHOLD).isAfter(dateOfBirth) ||
                LocalDate.now().minusYears(AGE_THRESHOLD).isEqual(dateOfBirth);
    }

    /**
     * Displays the cards in the player's hand.
     */
    public void showHand() {
        for (Card card : hand) {
            if (card != null) {
                logger.info("   " + card);
            } else {
                logger.error("Error: There is no card");
            }
        }
    }

    /**
     * Calculates and returns the total value of the player's hand.
     *
     * @return the total value of the hand
     */
    public int getHandValue() {
        Card card = new Card();
        return card.getCardsValue(hand);
    }

    /**
     * Draws a card from the deck and adds it to the player's hand.
     *
     * @param deck the deck to draw a card from
     */
    public Card drawCard(Deck deck) {
        Card card = deck.deal();
        if (hand == null) {
            hand = getHand();
        }
        if (hand.length <= numOfCards) {
            hand[hand.length - 1] = card;
        }
        return card;
    }


    /**
     * Draws cards from the deck and adds it to the player's hand.
     *
     * @param deck the deck to draw a card from
     */
    public Card[] drawCards(Deck deck) {
        if (this.hand == null) {
            this.hand = getHand();
        }
        for (int i = 0; i < hand.length; i++) {
            Card card = deck.deal();
            if (card != null) {
                hand[i] = card;
            }

        }
        return hand;
    }

    /**
     * Updates points based on the bet and the hand values of other players.
     *
     * @param betPoints the points to bet
     * @param otherPlayers the list of other players
     * @return the list of players with updated points, including this player
     */
    public List<Player> updatePoints(int betPoints, List<Player> otherPlayers) {
        int netPoints = 0;
        int handValue = this.getHandValue();

        for (Player other : otherPlayers) {
            if (this == other) continue;

            int otherHandValue = other.getHandValue();
            if (handValue > otherHandValue) {
                netPoints += betPoints;
                other.changePoints(-betPoints);
            } else if (handValue < otherHandValue) {
                netPoints -= betPoints;
                other.changePoints(betPoints);
            }
        }

        changePoints(netPoints);

        // Include this player in the list of updated players
        List<Player> allPlayers = new ArrayList<>(otherPlayers);
        if (!allPlayers.contains(this)) {
            allPlayers.add(this);
        }

        return allPlayers;
    }


    /**
     * Calculates and returns the player's age based on their date of birth.
     *
     * @return the age of the player
     */
    public int getAge() {
        LocalDate today = LocalDate.now();
        return Period.between(dateOfBirth, today).getYears();
    }

    /**
     * Checks if this player is equal to another object.
     *
     * @param o the object to compare this player against
     * @return true if the given object represents a Player equivalent to this player, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true; // Check if both references point to the same object
        }
        if (o == null || getClass() != o.getClass()) {
            return false; // Check if the other object is null or of a different class
        }
        Player player = (Player) o;
        // Compare individual fields for equality
        return playerId == player.playerId &&
                points == player.points &&
                isAdmin == player.isAdmin &&
                isLoggedIn == player.isLoggedIn &&
                username.equals(player.username) &&
                firstName.equals(player.firstName) &&
                lastName.equals(player.lastName) &&
                dateOfBirth.equals(player.dateOfBirth);
    }

    /**
     * Returns a hash code value for the player.
     *
     * @return a hash code value for this player based on its fields
     */
    @Override
    public int hashCode() {
        // Generate a hash code based on the object's fields
        return Objects.hash(playerId, username, firstName, lastName, dateOfBirth, points, isAdmin, isLoggedIn);
    }

    /**
     * Returns a string representation of the player, including key details such as
     * username, full name, age, points, admin status, and login status.
     *
     * @return a string representation of the player
     */
    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + getAge() +
                ", points=" + points +
                ", isAdmin=" + isAdmin +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }

}
