package com.topcard.dao.player;

import com.topcard.domain.Player;
import com.topcard.util.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerDaoImplTest {

    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockPreparedStatement;
    @Mock
    ResultSet mockResultSet;

    @InjectMocks PlayerDaoImpl playerDao = new PlayerDaoImpl();

    @Test
    public void testAddPlayerSuccess() throws Exception {
        Player player = new Player("mickey", "password", "Mickey", "Mouse", LocalDate.of(1990, 1, 1));

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1001);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            Player result = playerDao.addPlayer(player);

            assertNotNull(result);
            assertEquals(1001, result.getPlayerId());
            assertEquals("mickey", result.getUsername());
            verify(mockPreparedStatement, times(1)).executeUpdate();
        }
    }

    @Test
    public void testGetPlayerByIdFound() throws Exception {
        int testId = 101;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("player_id")).thenReturn(testId);
        when(mockResultSet.getString("username")).thenReturn("mickey");
        when(mockResultSet.getString("password")).thenReturn("password");
        when(mockResultSet.getString("first_name")).thenReturn("Mickey");
        when(mockResultSet.getString("last_name")).thenReturn("Mouse");
        when(mockResultSet.getDate("date_of_birth")).thenReturn(Date.valueOf("1990-01-01"));
        when(mockResultSet.getInt("points")).thenReturn(50);
        when(mockResultSet.getBoolean("is_admin")).thenReturn(false);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            Optional<Player> result = playerDao.getPlayerById(testId);

            assertTrue(result.isPresent());
            assertEquals("mickey", result.get().getUsername());
        }
    }

    @Test
    public void testGetPlayerByIdNotFound() throws Exception {
        int testId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            Optional<Player> result = playerDao.getPlayerById(testId);
            assertFalse(result.isPresent());
        }
    }
}
