package com.topcard.business;

import com.topcard.domain.Player;
import com.topcard.service.player.IPlayerService;
import com.topcard.service.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * PlayerManager is responsible for managing player-related operations.
 * It interacts with the IPlayerService to add, remove, update, and retrieve player information.
 * <p>
 * Author: Rajesh Rajchal
 * Date: 08/22/2025
 * Subject: MSSE 672 Component-Based Software Development
 * </p>
 */
@Component
public class PlayerManager {

    private final IPlayerService playerService;

    /**
     * Constructs a new PlayerManager and initializes the player service.
     */
    @Autowired
    public PlayerManager(IPlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Adds a new player.
     *
     * @param player the player to add
     */
    public boolean addPlayer(Player player) {
        return playerService.addPlayer(player);
    }

    /**
     * Adds multiple players.
     *
     * @param players the list of players to add
     */
    public void addPlayers(List<Player> players) {
        playerService.addPlayers(players);
    }

    /**
     * Removes a player by their ID.
     *
     * @param playerId the ID of the player to remove
     */
    public void removePlayer(int playerId) {
        playerService.removePlayer(playerId);
    }

    /**
     * Retrieves a player by their ID.
     *
     * @param playerId the ID of the player to retrieve
     * @return the player with the specified ID
     */
    public Player getPlayerById(int playerId) {
        return playerService.getPlayerById(playerId);
    }

    /**
     * Retrieves a player by their username.
     *
     * @param userName the username of the player to retrieve
     * @return the player with the specified username
     */
    public Player getPlayerByUsername(String userName) {
        return playerService.getPlayerByUsername(userName);
    }

    /**
     * Changes the points of a player.
     *
     * @param playerId the ID of the player whose points to change
     * @param points   the new points value
     */
    public void changePoints(int playerId, int points) {
        playerService.changePoints(playerId, points);
    }

    /**
     * Checks if a player is an admin.
     *
     * @param playerId the ID of the player to check
     * @return true if the player is an admin, false otherwise
     */
    public boolean isPlayerAdmin(int playerId) {
        return playerService.isPlayerAdmin(playerId);
    }

    /**
     * Makes a player an admin.
     *
     * @param playerId the ID of the player to make an admin
     */
    public void makePlayerAdmin(int playerId) {
        playerService.makePlayerAdmin(playerId);
    }

    /**
     * Updates the profile of a player.
     *
     * @param player the player with updated information
     */
    public void updateProfile(Player player) {
        playerService.updateProfile(player);
    }

    /**
     * Updates the profile of a player by their ID.
     *
     * @param playerId       the ID of the player to update
     * @param newFirstName   the new first name
     * @param newLastName    the new last name
     * @param newDateOfBirth the new date of birth
     */
    public void updateProfile(int playerId, String newFirstName, String newLastName, LocalDate newDateOfBirth) {
        playerService.updateProfile(playerId, newFirstName, newLastName, newDateOfBirth);
    }

    /**
     * Updates the profiles of multiple players.
     *
     * @param players the list of players with updated information
     */
    public void updateProfiles(List<Player> players) {
        playerService.updateProfiles(players);
    }

    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        return ((PlayerService)playerService).verifyPassword(plainPassword, encryptedPassword);
    }

    /**
     * Retrieves the points of a player by their ID.
     *
     * @param playerId the ID of the player whose points to retrieve
     * @return the points of the player
     */
    public int retrievePointForPlayer(int playerId) {
        return playerService.retrievePointForPlayer(playerId);
    }

    /**
     * Retrieves all players.
     *
     * @return the list of all players
     */
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }
}
