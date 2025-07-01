package com.topcard.domain;

import com.topcard.debug.Debug;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Game class represents a card game.
 * It manages the deck, players, and game state.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    private final Deck deck;
    private final List<Player> players;

    /**
     * Constructs a new Game with the specified players.
     *
     * @param players the list of players participating in the game
     */
    public Game(List<Player> players) {
        Debug.info("Game created.");
        this.deck = new Deck();
        this.players = players;
    }

    /**
     * Start game and plays the complete game by shuffling the deck,
     * dealing cards, showing hands, and displaying winners.
     */
    public void startGame() {
        deck.shuffle();
        dealCards();
    }

    /**
     * Displays the winners of the game.
     *
     * @param winners the list of winning players
     */
    public void displayWinners(List<Player> winners) {
        for (Player winner : winners) {
            System.out.println("Winner " + winner.getFirstName() + " " + winner.getLastName()
                    + " has " + winner.getHandValue());
        }
    }

    /**
     * Deals cards to each player.
     */
    public void dealCards() {
        for (Player player : players) {
            player.drawCards(deck);
        }
    }

    /**
     * Returns a list of players' hands.
     *
     * @return a list of players' hands
     */
    public List<Card[]> getHands() {
        List<Card[]> hand = new ArrayList<>();
        for (Player player : players) {
            hand.add(player.getHand());
        }
        return hand;
    }

    /**
     * Shows the hands of all players.
     */
    public void showHand() {
        for (Player player : players) {
            Debug.info("Player " + player.getFirstName() + " has ");
            player.showHand();
            Debug.info("Total Score: " + player.getHandValue());
        }
    }

    /**
     * Determines the winner(s) among the players based on hand value and card rankings.
     * The player(s) with the highest total points win(s).
     * In the event of tied point totals, specific card rankings determine the winner:
     * K > Q > J > 10 > 9 > 8 > 7 > 6 > 5 > 4 > 3 > 2 > A.
     * If multiple players have the highest points with the same total and card rankings,
     * they are all considered winners and receive the betting amount from the other players.
     *
     * @return the list of winning players
     */
    public List<Player> determineWinner() {
        List<Player> winners = new ArrayList<>();
        int highestValue = 0;

        // Determine the highest hand value
        for (Player player : players) {
            if (player.getHandValue() > highestValue) {
                highestValue = player.getHandValue();
            }
        }

        // Find players with the highest points
        for (Player player : players) {
            if (player.getHandValue() == highestValue) {
                winners.add(player);
            }
        }

        // If there is a tie, determine the winner based on card rankings
        if (winners.size() > 1) {
            winners = determineWinnerByCardRankings(winners);
        }

        return winners;
    }

    /**
     * Determines the winner(s) among tied players based on card rankings.
     *
     * @param tiedPlayers the list of players with tied hand values
     * @return the list of winning players
     */
    public List<Player> determineWinnerByCardRankings(List<Player> tiedPlayers) {
        List<Player> finalWinners = new ArrayList<>();
        int highestPrecedence = Integer.MAX_VALUE;

        // Determine the highest precedence card
        for (Player player : tiedPlayers) {
            for (Card card : player.getHand()) {
                int precedence = card.getRank().getPrecedence();
                if (precedence < highestPrecedence) {
                    highestPrecedence = precedence;
                }
            }
        }

        // Find players with the highest precedence card
        for (Player player : tiedPlayers) {
            for (Card card : player.getHand()) {
                int precedence = card.getRank().getPrecedence();
                if (precedence == highestPrecedence) {
                    finalWinners.add(player);
                    break;
                }
            }
        }

        return finalWinners;
    }

    /**
     * Executes a betting round for the given players.
     *
     * The first player in the list is treated as the main player who places the bet,
     * and the points are updated based on the hand values of all players.
     *
     * @param points the amount of points each player bets
     * @param players the list of Player objects where the first player is the main player
     * @return the list of players with updated points
     */
    public List<Player> betAndUpdatePlayerPoints(int points, List<Player> players) {
        if (players == null || players.isEmpty()) {
            return players; // No players to bet with, return the original list
        }

        // The main player is the first player in the list
        Player mainPlayer = players.get(0);

        // The rest of the players
        List<Player> otherPlayers = players.subList(1, players.size());

        // Call updatePoints with main player and other players, and return the updated list
        return mainPlayer.updatePoints(points, otherPlayers);
    }

    /**
     * Returns the list of players in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return this.players;
    }
}
