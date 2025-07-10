package com.topcard.service;

import com.topcard.dao.player.IPlayerDao;
import com.topcard.domain.Player;
import com.topcard.service.player.PlayerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerServiceTest {

    @Mock
    private IPlayerDao mockDao;
    private PlayerService playerService;

    @Before
    public void setUp() {
        playerService = new PlayerService(mockDao); // Inject mock into service
    }

    @Test
    public void testAddPlayer() {
        Player player = new Player("Mickey", "mouse123", "Mickey", "Mouse", LocalDate.of(1928, 11, 18));
        lenient().when(mockDao.getPlayerByUsername("mickey")).thenReturn(Optional.empty());
        when(mockDao.addPlayer(any(Player.class))).thenReturn(player);

        boolean result = playerService.addPlayer(player);

        assertTrue(result);
        verify(mockDao).addPlayer(any(Player.class));
    }

    @Test
    public void testGetAllPlayers() {
        Player p1 = new Player("donald", "duck123", "Donald", "Duck", LocalDate.of(1934, 6, 9));
        Player p2 = new Player("goofy", "goofy123", "Goofy", "Goof", LocalDate.of(1932, 5, 25));

        when(mockDao.getAllPlayers()).thenReturn(Arrays.asList(p1, p2));

        assertEquals(2, playerService.getAllPlayers().size());
        verify(mockDao).getAllPlayers();
    }

    @Test
    public void testRemovePlayer() {
        when(mockDao.deletePlayer(1)).thenReturn(true);
        playerService.removePlayer(1);
        verify(mockDao).deletePlayer(1);
    }

    @Test
    public void testGetPlayerById_NotFound() {
        when(mockDao.getPlayerById(999)).thenReturn(Optional.empty());
        Player result = playerService.getPlayerById(999);
        assertNull(result);
    }
}
