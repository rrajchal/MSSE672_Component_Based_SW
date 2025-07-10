package com.topcard.service.player;

import com.topcard.dao.player.IPlayerDao;
import com.topcard.dao.player.PlayerDaoImpl;
import com.topcard.domain.Player;
import com.topcard.exceptions.TopCardException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The PlayerService class implements the IPlayerService interface and provides
 * the business logic for player-related operations in the TopCard game.
 * <p>
 * This includes adding, removing, updating, and retrieving player information,
 * as well as managing player points and admin status.
 * </p>
 * <p>
 * Author: Rajesh Rajchal
 * Date: 06/30/2025
 * </p>
 */
public class PlayerService implements IPlayerService {

    private static final Logger logger = LogManager.getLogger(PlayerService.class);

    private final IPlayerDao playerDao;

    /*
    private static final Path dataFilePath;

    // Move file data to mysql database
    static {
        Properties properties = new Properties();
        Path configFilePath = Paths.get("config", "config.properties");

        try (InputStream input = Files.newInputStream(configFilePath)) {
            // Load the properties file
            properties.load(input);
            dataFilePath = Paths.get(properties.getProperty("FILE_PATH"));
        } catch (IOException ex) {
            throw new TopCardException("Failed to load configuration properties. " + ex.getMessage());
        }
    }
    */
    public PlayerService() {
        this.playerDao = new PlayerDaoImpl(); // Instantiate the DAO implementation
    }

    public PlayerService(IPlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    @Override
    public boolean addPlayer(Player player) {
        if (playerDao.getPlayerByUsername(player.getUsername()).isEmpty()) {
            //player.setPlayerId(getNewId());
            player.setUsername(player.getUsername().toLowerCase());
            player.setPassword(encryptPassword(player.getPassword())); // Encrypt the password
            Player addedPlayer = playerDao.addPlayer(player);
            if (addedPlayer != null) {
                logger.info("Player added: " + addedPlayer.getUsername() + " with ID: " + addedPlayer.getPlayerId());
                return true;
            } else {
                logger.error("Failed to add player to database: " + player.getUsername());
                return false;
            }
        } else {
            logger.warn("Player with username '" + player.getUsername() + "' already exists. No player added.");
            return false;
        }
    }

    @Override
    public void addPlayers(List<Player> players) {
        players.forEach(this::addPlayer);
    }

    @Override
    public void removePlayer(int playerId) {
        boolean removed = playerDao.deletePlayer(playerId);

        if (removed) {
            logger.info("Player removed from DB: " + playerId);
        } else {
            logger.warn("Player with ID " + playerId + " not found or could not be removed.");
        }
    }

    @Override
    public Player getPlayerById(int playerId) {
        return playerDao.getPlayerById(playerId).orElseGet(() -> {
            logger.debug("Player not found with ID: " + playerId);
            return null;
        });
    }

    @Override
    public Player getPlayerByUsername(String userName) {
        return playerDao.getPlayerByUsername(userName).orElseGet(() -> {
                    logger.debug("Player not found with username: " + userName);
                    return null;
                });
    }

    @Override
    public void changePoints(int playerId, int points) {
        Optional<Player> optionalPlayer = playerDao.getPlayerById(playerId);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            player.setPoints(points); // Update points in the object
            updateProfile(player, false); // Keep password unchanged
        } else {
            logger.warn("Cannot change points. Player with ID " + playerId + " not found.");
        }
    }

    @Override
    public boolean isPlayerAdmin(int playerId) {
        Optional<Player> optionalPlayer = playerDao.getPlayerById(playerId);
        return optionalPlayer.map(Player::isAdmin).orElse(false);
    }

    @Override
    public void makePlayerAdmin(int playerId) {
        Optional<Player> optionalPlayer = playerDao.getPlayerById(playerId);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            player.setAdmin(true);
            updateProfile(player);
        } else {
            logger.warn("Cannot make admin. Player with ID " + playerId + " not found.");
        }
    }

    private void updateProfile(Player player, boolean changePassword) {
        if (!changePassword) {
            playerDao.getPlayerById(player.getPlayerId())
                     .ifPresent(existingPlayer -> player.setPassword(existingPlayer.getPassword()));
        }
        updateProfile(player);
        logger.info("Player's points updated: " + player);
    }

    @Override
    public void updateProfile(Player player) {
        Optional<Player> optionalExistingPlayer = playerDao.getPlayerById(player.getPlayerId());
        logger.debug("Attempting to update player: " + player);
        if (optionalExistingPlayer.isPresent()) {
            Player existingPlayer = optionalExistingPlayer.get();

            // Only re-hash if the new password is different and not already hashed
            if (!BCrypt.checkpw(player.getPassword(), existingPlayer.getPassword())) {
                    player.setPassword(encryptPassword(player.getPassword()));
            } else {
                // If the submitted password matches the existing hash, keep the existing hash
                player.setPassword(existingPlayer.getPassword());
            }

            boolean updated = playerDao.updatePlayer(player);
            if (updated) {
                logger.info("Player updated: " + player.getUsername());
            } else {
                logger.warn("Failed to update player: " + player.getUsername());
            }
        } else {
            logger.warn("Player with ID " + player.getPlayerId() + " not found for update.");
        }
    }

    @Override
    public void updateProfile(int playerId, String newFirstName, String newLastName, LocalDate newDateOfBirth) {
        Optional<Player> optionalPlayer = playerDao.getPlayerById(playerId);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            player.setFirstName(newFirstName);
            player.setLastName(newLastName);
            player.setDateOfBirth(newDateOfBirth);
            updateProfile(player);
        } else {
            logger.warn("Cannot update profile. Player with ID " + playerId + " not found.");
        }
    }

    @Override
    public void updateProfiles(List<Player> players) {
        players.forEach(this::updateProfile);
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerDao.getAllPlayers();
    }

    @Override
    public int retrievePointForPlayer(int playerId) {
        Optional<Player> optionalPlayer = playerDao.getPlayerById(playerId);
        if (optionalPlayer.isPresent()) {
            return optionalPlayer.get().getPoints();
        }
        throw new TopCardException("Player not found with ID: " + playerId);
    }

    /**
     * Encrypts a password using BCrypt. This way the data file will have encrypted text
     *
     * @param password the plain text password
     * @return the encrypted password
     */
    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifies a plain text password against an encrypted password.
     *
     * @param plainPassword the plain text password
     * @param encryptedPassword the encrypted password
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        return BCrypt.checkpw(plainPassword, encryptedPassword);
    }

    /**
     * Deletes all data in the player file.
     * This function is made private and used in Unit Test.
     */
    private void deleteAllPlayersData() {
        playerDao.deleteAllPlayersData();
    }

    /**
     * A functional interface for evaluating conditions on lines from the players.csv file.
     */
    @FunctionalInterface
    private interface Predicate<T> {
        boolean test(T t);
    }
}
