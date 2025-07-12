package com.topcard.dao.player;

import com.topcard.domain.Player;
import com.topcard.util.HibernateUtil;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerDaoImplTest {

    // Mocks for Hibernate components
    @Mock private SessionFactory mockSessionFactory;
    @Mock private Session mockSession;
    @Mock private Transaction mockTransaction;

    // Mocks for query execution
    @Mock private Query<Player> mockQuery;
    @Mock private HibernateCriteriaBuilder mockCriteriaBuilder;
    @Mock private JpaCriteriaQuery<Player> mockCriteriaQuery;
    @Mock private JpaRoot<Player> mockRoot;

    private MockedStatic<HibernateUtil> hibernateStatic;

    private PlayerDaoImpl playerDao;
    private Player samplePlayer;

    @BeforeEach
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setup() {
        samplePlayer = new Player("goofy", "password", "Goofy", "Dog", LocalDate.of(1988, 4, 4));
        samplePlayer.setPlayerId(9999);

        playerDao = new PlayerDaoImpl();

        hibernateStatic = mockStatic(HibernateUtil.class);
        hibernateStatic.when(HibernateUtil::getSessionFactory).thenReturn(mockSessionFactory);

        lenient().when(mockSessionFactory.openSession()).thenReturn(mockSession);
        lenient().when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        lenient().when(mockSession.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);

        // Common Criteria API chaining stubs
        lenient().doReturn(mockCriteriaQuery).when(mockCriteriaBuilder).createQuery(Player.class);
        lenient().doReturn(mockRoot).when(mockCriteriaQuery).from(Player.class);
        lenient().when(mockCriteriaQuery.select(mockRoot)).thenReturn(mockCriteriaQuery);
        lenient().when(mockCriteriaQuery.where(any(Predicate.class))).thenReturn(mockCriteriaQuery);
        lenient().when(mockSession.createSelectionQuery(mockCriteriaQuery)).thenReturn(mockQuery);
    }

    @AfterEach
    public void tearDown() {
        if (hibernateStatic != null) {
            hibernateStatic.close();
        }
    }

    @Test
    public void testGetAllPlayers() {
        List<Player> mockList = Collections.singletonList(samplePlayer);
        when(mockSession.createQuery(mockCriteriaQuery)).thenReturn(mockQuery); // Stub createQuery for Criteria API
        when(mockQuery.getResultList()).thenReturn(mockList);

        List<Player> result = playerDao.getAllPlayers();

        assertEquals(1, result.size());
        assertEquals("goofy", result.get(0).getUsername());
        verify(mockCriteriaBuilder).createQuery(Player.class);
        verify(mockCriteriaQuery).from(Player.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockSession).createQuery(mockCriteriaQuery);
        verify(mockQuery).getResultList();
        verify(mockSession).close();
    }


    @Test
    public void testAddPlayer() {
        Player result = playerDao.addPlayer(samplePlayer);

        assertNotNull(result);
        assertEquals(samplePlayer, result);
        assertEquals(9999, result.getPlayerId());
        verify(mockSession).persist(samplePlayer);
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }

    @Test
    public void testGetPlayerById() {
        when(mockSession.get(Player.class, samplePlayer.getPlayerId())).thenReturn(samplePlayer);

        Optional<Player> result = playerDao.getPlayerById(9999);

        assertTrue(result.isPresent());
        assertEquals("goofy", result.get().getUsername());
        verify(mockSession).get(Player.class, 9999);
        verify(mockSession).close();
    }

    @Test
    public void testGetPlayerByIdNotFound() {
        when(mockSession.get(Player.class, -999)).thenReturn(null);

        Optional<Player> result = playerDao.getPlayerById(-999);

        assertFalse(result.isPresent());
        verify(mockSession).get(Player.class, -999);
        verify(mockSession).close();
    }

    @Test
    public void testDeletePlayer() {
        when(mockSession.get(Player.class, 9999)).thenReturn(samplePlayer);

        boolean deleted = playerDao.deletePlayer(9999);

        assertTrue(deleted);
        verify(mockSession).remove(samplePlayer);
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }

    @Test
    public void testUpdatePlayerWithMockSession() {
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);

        boolean result = playerDao.updatePlayer(samplePlayer);

        assertTrue(result);
        verify(mockSession).merge(samplePlayer); // 🛠 Correct Hibernate method for updates
        verify(mockTransaction).commit();
        verify(mockSession).close();
    }
}
