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
        IGameService gameService = ServiceFactory.createService(IGameService.class, players);
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
    public void testCardServiceNotShuffled() {
        ICardService cardService = ServiceFactory.createService(CardService.class);
        Card firstCard = cardService.drawCard();
        Card secondCard = cardService.drawCard();
        Card thirdCard = cardService.drawCard();
        assertEquals(new Card(Card.Suit.HEARTS, Card.Rank.ACE), firstCard);
        assertEquals(new Card(Card.Suit.HEARTS, Card.Rank.TWO), secondCard);
        assertNotEquals(new Card(Card.Suit.HEARTS, Card.Rank.TWO), thirdCard);
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

    // Verifies that ServiceFactory returns the same instance for repeated service requests to confirm singleton behavior
    @Test
    public void testSingletonInstanceReuse() {
        ICardService firstInstance = ServiceFactory.createService(CardService.class);
        ICardService secondInstance = ServiceFactory.createService(CardService.class);
        assertSame(firstInstance, secondInstance, "ServiceFactory should reuse the same instance");
    }

    // Tests ServiceFactory's ability to instantiate services using parameterized constructors via reflection.
    @Test
    public void testConstructorWithArguments() {
        List<Player> players = getPlayers();
        IGameService gameService = ServiceFactory.createService(IGameService.class, players);
        assertNotNull(gameService, "GameService should be instantiated with players");
        assertEquals(players.size(), gameService.getPlayers().size(), "Players should be correctly passed to GameService");
    }

}
