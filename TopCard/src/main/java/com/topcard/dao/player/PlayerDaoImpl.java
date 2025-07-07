package com.topcard.dao.player;

import com.topcard.domain.Player;
import com.topcard.exceptions.TopCardException;
import com.topcard.util.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerDaoImpl implements IPlayerDao {

    private static final Logger logger = LogManager.getLogger(PlayerDaoImpl.class);

    private static final String INSERT_PLAYER_SQL = "INSERT INTO players (username, password_hash, first_name, last_name, date_of_birth, points, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PLAYER_BY_ID_SQL = "SELECT * FROM players WHERE player_id = ?";
    private static final String SELECT_PLAYER_BY_USERNAME_SQL = "SELECT * FROM players WHERE username = ?";
    private static final String UPDATE_PLAYER_SQL = "UPDATE players SET username = ?, password_hash = ?, first_name = ?, last_name = ?, date_of_birth = ?, points = ?, is_admin = ? WHERE player_id = ?";
    private static final String DELETE_PLAYER_SQL = "DELETE FROM players WHERE player_id = ?";
    private static final String SELECT_ALL_PLAYERS_SQL = "SELECT * FROM players";
    private static final String DELETE_ALL_PLAYERS_SQL = "DELETE FROM players";

    @Override
    public Player addPlayer(Player player) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PLAYER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, player.getUsername());
            preparedStatement.setString(2, player.getPassword());
            preparedStatement.setString(3, player.getFirstName());
            preparedStatement.setString(4, player.getLastName());
            preparedStatement.setDate(5, Date.valueOf(player.getDateOfBirth()));
            preparedStatement.setInt(6, player.getPoints());
            preparedStatement.setBoolean(7, player.isAdmin());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        player.setPlayerId(generatedKeys.getInt(1)); // Set the auto-generated ID
                        logger.info("Player added to DB: " + player.getUsername());
                        return player;
                    }
                }
            }
            logger.warn("Failed to add player to DB: " + player.getUsername());
            return null;

        } catch (SQLException e) {
            logger.error("Database error adding player: " + player.getUsername(), e);
            throw new TopCardException("Error adding player to database: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Player> getPlayerById(int playerId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PLAYER_BY_ID_SQL)) {

            preparedStatement.setInt(1, playerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToPlayer(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Database error getting player by ID: " + playerId, e);
            throw new TopCardException("Error retrieving player by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Player> getPlayerByUsername(String username) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PLAYER_BY_USERNAME_SQL)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToPlayer(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Database error getting player by username: " + username, e);
            throw new TopCardException("Error retrieving player by username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updatePlayer(Player player) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PLAYER_SQL)) {

            preparedStatement.setString(1, player.getUsername());
            preparedStatement.setString(2, player.getPassword()); // This is the encrypted hash
            preparedStatement.setString(3, player.getFirstName());
            preparedStatement.setString(4, player.getLastName());
            preparedStatement.setDate(5, Date.valueOf(player.getDateOfBirth()));
            preparedStatement.setInt(6, player.getPoints());
            preparedStatement.setBoolean(7, player.isAdmin());
            preparedStatement.setInt(8, player.getPlayerId()); // WHERE clause

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Player updated in DB: " + player.getUsername());
                return true;
            }
            logger.warn("Failed to update player in DB: " + player.getUsername() + ". Player not found or no changes made.");
            return false;

        } catch (SQLException e) {
            logger.error("Database error updating player: " + player.getUsername(), e);
            throw new TopCardException("Error updating player in database: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletePlayer(int playerId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PLAYER_SQL)) {

            preparedStatement.setInt(1, playerId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Player deleted from DB with ID: " + playerId);
                return true;
            }
            logger.warn("Failed to delete player from DB with ID: " + playerId + ". Player not found.");
            return false;

        } catch (SQLException e) {
            logger.error("Database error deleting player with ID: " + playerId, e);
            throw new TopCardException("Error deleting player from database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_PLAYERS_SQL)) {

            while (resultSet.next()) {
                players.add(mapResultSetToPlayer(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Database error getting all players.", e);
            throw new TopCardException("Error retrieving all players from database: " + e.getMessage(), e);
        }
        return players;
    }

    @Override
    public void deleteAllPlayersData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            int affectedRows = statement.executeUpdate(DELETE_ALL_PLAYERS_SQL);
            logger.info("Deleted " + affectedRows + " players from DB.");

        } catch (SQLException e) {
            logger.error("Database error deleting all players.", e);
            throw new TopCardException("Error deleting all players from database: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to map a ResultSet row to a Player object.
     * @param resultSet The ResultSet containing player data.
     * @return A Player object populated with data from the current ResultSet row.
     * @throws SQLException If a SQL error occurs while reading the ResultSet.
     */
    private Player mapResultSetToPlayer(ResultSet resultSet) throws SQLException {
        int playerId = resultSet.getInt("player_id");
        String username = resultSet.getString("username");
        String passwordHash = resultSet.getString("password_hash");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
        int points = resultSet.getInt("points");
        boolean isAdmin = resultSet.getBoolean("is_admin");

        Player player = new Player(username, passwordHash, firstName, lastName, dateOfBirth);
        player.setPlayerId(playerId);
        player.setPoints(points);
        player.setAdmin(isAdmin);
        return player;
    }
}