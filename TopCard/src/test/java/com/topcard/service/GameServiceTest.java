package com.topcard.service;

import static org.junit.Assert.*;

import com.topcard.domain.PlayerTest;
import com.topcard.exceptions.TopCardException;
import com.topcard.service.game.GameService;
import com.topcard.service.player.PlayerService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import com.topcard.domain.Player;

public class GameServiceTest {

    private GameService gameService;
    private PlayerService playerService;
    private Player player1;
    private Player player2;
    private Player player3;

    @Before
    public void setUp() {
        player1 = new Player("username1", "password", "firstName1", "lastName1", LocalDate.of(2000, 1, 1));
        player2 = new Player("username2", "password", "firstName2", "lastName2", LocalDate.of(2000, 2, 1));
        player3 = new Player("username3", "password", "firstName3", "lastName3", LocalDate.of(2000, 3, 1));
        List<Player> players = Arrays.asList(player1, player2, player3);
        gameService = new GameService(players);
        playerService = new PlayerService();
    }

    @Test
    public void testStartGame() {
        gameService.startGame();
        assertNotNull(player1.getHand());
        assertNotNull(player2.getHand());
        assertNotNull(player3.getHand());
        assertNotEquals(0, player1.getHand().length);
        assertNotEquals(0, player2.getHand().length);
        assertNotEquals(0, player3.getHand().length);
    }

    @Test
    public void testPlayCompleteGame() {
        gameService.startGame();
        assertNotNull(player1.getHand());
        assertNotNull(player2.getHand());
        assertNotNull(player3.getHand());
        assertNotEquals(0, player1.getHand().length);
        assertNotEquals(0, player2.getHand().length);
        assertNotEquals(0, player3.getHand().length);
    }

    @Test
    public void testDataChange() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // Remove all data
//        Method method = PlayerService.class.getDeclaredMethod("deleteAllPlayersData");
//        method.setAccessible(true);
//        method.invoke(playerService);
//
//        PlayerTest playerTest = new PlayerTest();
//        List<Player> players = playerTest.generatePlayers();
//        gameService = new GameService(players);
//        playerService.addPlayers(players);
//        gameService.startGame();
//        gameService.showHands();
//        List<Player> winners = gameService.determineWinner();
//        gameService.displayWinners(winners);
//
//        List<Player> updatedPlayers = gameService.executeBettingRound(10);
//
//        gameService.updateProfiles(updatedPlayers);
    }

}

