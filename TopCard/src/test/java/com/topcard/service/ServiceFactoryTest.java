package com.topcard.service;

import com.topcard.domain.Card;
import com.topcard.domain.Player;
import com.topcard.domain.PlayerTest;
import com.topcard.service.card.CardService;
import com.topcard.service.card.ICardService;
import com.topcard.service.factory.ServiceFactory;
import com.topcard.service.game.GameService;
import com.topcard.service.game.IGameService;
import com.topcard.service.player.IPlayerService;
import com.topcard.service.player.PlayerService;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

public class ServiceFactoryTest {

    @Test
    public void testCreateService() {
        List<Player> players = getPlayers();
        IGameService gameService = ServiceFactory.createService(GameService.class, players);
        assertNotNull(gameService);
        assertInstanceOf(GameService.class, gameService);
    }

    @Test
    public void testCreatePlayerService() {
        IPlayerService playerService = ServiceFactory.createService(PlayerService.class);
        assertNotNull(playerService);
        assertInstanceOf(PlayerService.class, playerService);
    }

    @Test
    public void testCreateCardService() {
        ICardService cardService = ServiceFactory.createService(CardService.class);
        assertNotNull(cardService);
        assertInstanceOf(CardService.class, cardService);
    }

    @Test
    public void testGameServiceStartGame() {
        ICardService cardService = ServiceFactory.createService(CardService.class);
        List<Player> players = getPlayers();
        IGameService gameService = ServiceFactory.createService(GameService.class, players);
        assertNotNull(gameService);
        gameService.startGame();

        assertEquals(3, players.get(0).getHand().length);  // three cards each player
        assertTrue(cardService.getCardsValue(players.get(0).getHand()) > 2); // Minimum total value of all three cards is at least 3.
    }

    @Test
    public void testPlayerServiceAddPlayer() {
        IPlayerService playerService = ServiceFactory.createService(PlayerService.class);
        // deleteAllPlayersData(playerService);  // removes all data, let's not remove; rather let's remove one player only

        Player player = playerService.getPlayerByUsername("mickey");
        if (player != null) {
            playerService.removePlayer(player.getPlayerId());
        }

        int initialNumberOfPlayers = playerService.getAllPlayers().size();
        System.out.println(initialNumberOfPlayers);
        player = new Player("mickey", "password", "Mickey", "Mouse", LocalDate.of(1990, 1, 1));

        playerService.addPlayer(player);  // added the player.
        int finalNumberOfPlayers = playerService.getAllPlayers().size();
        System.out.println(finalNumberOfPlayers);
        assertEquals(1, finalNumberOfPlayers - initialNumberOfPlayers);
    }

    @Test
    public void testCardServiceNotShuffled() {
        ICardService cardService = ServiceFactory.createService(CardService.class);
        Card firstCard = cardService.drawCard();
        Card secondCard = cardService.drawCard();
        Card thirdCard = cardService.drawCard();
        assertEquals(firstCard, new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        assertEquals(secondCard, new Card(Card.Suit.HEARTS, Card.Rank.TWO));
        assertNotEquals(thirdCard, new Card(Card.Suit.HEARTS, Card.Rank.TWO));
    }

    @Test
    public void deleteAllPlayersData() {
        IPlayerService playerService = ServiceFactory.createService(PlayerService.class);
        // Call the private method using reflection
        try {
            Method method = PlayerService.class.getDeclaredMethod("deleteAllPlayersData");
            method.setAccessible(true);
            method.invoke(playerService);

            // Check if all data has been deleted
            List<Player> allPlayers = playerService.getAllPlayers();
            assertTrue(allPlayers.isEmpty());
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    private List<Player> getPlayers() {
        PlayerTest playerTest = new PlayerTest();
        return playerTest.generatePlayers();
    }

    private void deleteAllPlayersData(IPlayerService playerService) {
        try {
            Method method = PlayerService.class.getDeclaredMethod("deleteAllPlayersData");
            method.setAccessible(true);
            method.invoke(playerService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete all player data", e);
        }
    }
}
