package com.topcard.util;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {
    @Test
    public void testGetConnectionReturnsValidConnection() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(2), "Connection should be valid");
            assertFalse(connection.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("Exception thrown while getting connection: " + e.getMessage());
        }
    }

    @Test
    public void testCloseConnectionActuallyClosesIt() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            DatabaseConnection.closeConnection();
            assertTrue(connection.isClosed(), "Connection should be closed after calling closeConnection()");
        } catch (SQLException e) {
            fail("Exception thrown while closing connection: " + e.getMessage());
        }
    }
}