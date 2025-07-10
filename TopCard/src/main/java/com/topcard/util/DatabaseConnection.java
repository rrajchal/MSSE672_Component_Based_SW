package com.topcard.util;

import com.topcard.exceptions.TopCardException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
    private static final String DB_URL;
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;

    private static Connection connection = null;

    static {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("config.properties not found on classpath.");
            }
            properties.load(input);
            DB_URL = properties.getProperty("DB_URL");
            DB_USERNAME = properties.getProperty("DB_USERNAME");
            DB_PASSWORD = properties.getProperty("DB_PASSWORD");

            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver registered successfully.");

        } catch (IOException e) {
            logger.fatal("Failed to load database configuration properties. " + e.getMessage(), e);
            throw new TopCardException("Failed to load database configuration properties: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.fatal("MySQL JDBC Driver not found. Make sure mysql-connector-java.jar is in your classpath. " + e.getMessage(), e);
            throw new TopCardException("MySQL JDBC Driver not found: " + e.getMessage(), e);
        }
    }

    /**
     * Provides a single, shared database connection.
     * If the connection is null, closed, or invalid, a new one is established.
     *
     * @return A valid JDBC Connection object.
     * @throws SQLException If a database access error occurs or the URL is null.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(5)) { // 5-second timeout for validation
            logger.debug("Attempting to establish NEW DB connection: " + DB_URL);
            // create a singleton object
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            logger.info("Successfully established a new database connection.");
        } else {
            logger.debug("Reusing existing database connection.");
        }
        return connection;
    }

    /**
     * Closes the single database connection.
     * Call this method when your application is shutting down.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.info("Database connection closed successfully.");
                }
            } catch (SQLException e) {
                logger.error("Error closing database connection.", e);
            } finally {
                connection = null;
            }
        }
    }
}