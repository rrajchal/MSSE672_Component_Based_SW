package com.topcard.service.game;

import com.topcard.domain.Card;
import com.topcard.domain.Player;
import java.util.List;

/**
 * The IGameService interface defines the contract for the GameService.
 * It includes methods for starting the game, dealing cards, executing betting rounds,
 * determining winners, updating player profiles, and displaying winners.
 * <p>
 *  Author: Rajesh Rajchal
 *  Date: 06/30/2025
 */
public interface IGameService {

    /**
     * Starts the game by shuffling the deck and dealing cards to players.
     */
    void startGame();

    /**
     * Returns a list of players' hands.
     *
     * @return a list of players' hands
     */
    List<Card[]> getHands();

    /**
     * Displays cards of all players
     */
    void showHands();

    /**
     * Deal cards to players
     */
    void dealCards();

    /**
     * Executes a betting round for the given players.
     * <p>
     * Must start game (startGame()) first before calling this function
     * The first player in the list is treated as the main player who places the bet,
     * and the points are updated based on the hand values of all players.
     *
     * @param points the amount of points each player bets
     * @return the list of players with updated points
     */
    List<Player> executeBettingRound(int points);

    /**
     * Determines the winner(s) among the players based on hand value and card rankings.
     *
     * @return the list of winning players
     */
    List<Player> determineWinner();

    /**
     * Updates the profile information a player.
     *
     * @param player the player whose profile needs to be updated
     */
    void updateProfile(Player player);

    /**
     * Updates the profile information for players.
     *
     * @param players list of players whose profile need to be updated
     */
    void updateProfiles(List<Player> players);

    /**
     * Updates just player's points
     *
     * @param player whose point needs to be updated
     */
    void updatePoints(Player player);

    /**
     * Update points of all players
     * @param players list of players whose points need to be updated
     */
    void updatePoints(List<Player> players);

    /**
     * Displays the winners of the game.
     *
     * @param winners the list of winning players
     */
    void displayWinners(List<Player> winners);

    /**
     * Provides players of the game
     *
     * @return current players in the game
     */
    List<Player> getPlayers();
}
