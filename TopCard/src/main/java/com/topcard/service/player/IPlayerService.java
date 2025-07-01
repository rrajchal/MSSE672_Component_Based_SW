package com.topcard.service.player;

import com.topcard.domain.Player;
import java.time.LocalDate;
import java.util.List;

/**
 * The IPlayerService interface defines the contract for player-related operations in the TopCard game.
 * <p>
 * It includes methods for adding, removing, updating, and retrieving player information, as well as
 * managing player points and checking admin status.
 * </p>
 * <p>
 * Author: Rajesh Rajchal
 * Date: 11/03/2024
 * </p>
 */
public interface IPlayerService {
    /**
     * Adds a new player to the system.
     *
     * @param player the player to add
     * @return true if a player is added; otherwise, false
     */
    boolean addPlayer(Player player);

    /**
     * Adds players to the system.
     *
     * @param players list of players to add
     */
    void addPlayers(List<Player> players);

    /**
     * Removes a player from the system.
     *
     * @param playerId the ID of the player to remove
     */
    void removePlayer(int playerId);

    /**
     * Retrieves a player by their ID.
     *
     * @param playerId the ID of the player to retrieve
     * @return the player with the specified ID
     */
    Player getPlayerById(int playerId);

    /**
     *
     * @param userName the username of the player to retrieve
     * @return the player with the specified username
     */
    Player getPlayerByUsername(String userName);

    /**
     * Adds or deducts points to a player's score.
     * Players gain points when they win, lose when lost
     * Also, player can buy points
     *
     * @param playerId the ID of the player
     * @param points the number of points to add
     */
    void changePoints(int playerId, int points);

    /**
     * Checks if a player is an admin.
     *
     * @param playerId the ID of the player to check
     * @return true if the player is an admin, false otherwise
     */
    boolean isPlayerAdmin(int playerId);

    /**
     * Makes the player an admin by setting the isAdmin flag to true.
     *
     * @param playerId the ID of the player to be made an admin
     */
    void makePlayerAdmin(int playerId);

    /**
     * Updates a player's profile.
     *
     * @param player the player with updated information
     */
    void updateProfile(Player player);

    /**
     * Updates a player's profile by their ID.
     *
     * @param playerId the ID of the player to update
     * @param newFirstName the new first name
     * @param newLastName the new last name
     * @param newDateOfBirth the new date of birth
     */
    void updateProfile(int playerId, String newFirstName, String newLastName, LocalDate newDateOfBirth);

    /**
     * Updates players' profile.
     *
     * @param players list of player to be updated
     */
    void updateProfiles(List<Player> players);

    /**
     * Retrieves point from the data file for a give player id
     *
     * @param playerId
     * @return point of a given player id.
     */
    int retrievePointForPlayer(int playerId);

    /**
     * Retrieves all players from the file and returns a list of Player objects.
     *
     * @return a list of all players
     */
    List<Player> getAllPlayers();
}
