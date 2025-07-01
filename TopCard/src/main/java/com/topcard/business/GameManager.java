package com.topcard.business;

import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.service.factory.ServiceFactory;
import com.topcard.service.game.GameService;
import com.topcard.service.game.IGameService;

import java.util.List;

/**
 * GameManager is responsible for managing game-related operations.
 * It interacts with the IGameService to start the game, deal cards, execute betting rounds,
 * update player profiles, determine winners, and display winners.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
public class GameManager {
    private final IGameService gameService;

    /**
     * Constructs a new GameManager and initializes the game service with the provided players.
     *
     * @param players the list of players participating in the game
     */
    public GameManager(List<Player> players) {
        this.gameService = ServiceFactory.createService(GameService.class, players);
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        gameService.startGame();
    }

    /**
     * Retrieves the hands of all players.
     *
     * @return a list of arrays of cards representing the players' hands
     */
    public List<Card[]> getHands() {
        return gameService.getHands();
    }

    /**
     * Displays the hands of all players.
     */
    public void showHands() {
        gameService.showHands();
    }

    /**
     * Deals cards to all players.
     */
    public void dealCards() {
        gameService.dealCards();
    }

    /**
     *
     * @return players of the game
     */
    public List<Player> getPlayers() {
        return gameService.getPlayers();
    }

    /**
     * Executes a betting round with the specified points.
     *
     * @param points the points for the betting round
     * @return the list of players with updated points after the betting round
     */
    public List<Player> executeBettingRound(int points) {
        return gameService.executeBettingRound(points);
    }

    /**
     * Determines the winner(s) of the game.
     *
     * @return the list of winning players
     */
    public List<Player> determineWinner() {
        return gameService.determineWinner();
    }

    /**
     * Updates the profile of a player.
     *
     * @param player the player with updated information
     */
    public void updateProfile(Player player) {
        gameService.updateProfile(player);
    }

    /**
     * Updates the profiles of multiple players.
     *
     * @param players the list of players with updated information
     */
    public void updateProfiles(List<Player> players) {
        gameService.updateProfiles(players);
    }

    /**
     * Displays the winners of the game.
     *
     * @param winners the list of winning players
     */
    public void displayWinners(List<Player> winners) {
        gameService.displayWinners(winners);
    }
}
