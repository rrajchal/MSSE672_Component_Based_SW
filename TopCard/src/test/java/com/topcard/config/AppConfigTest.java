package com.topcard.config;

import com.topcard.service.card.ICardService;
import com.topcard.service.game.IGameService;
import com.topcard.service.player.IPlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for verifying Spring bean initialization in AppConfig.
 * Ensures all service beans are correctly instantiated and wired.
 */
class AppConfigTest {

    // Validates that ICardService, IPlayerService, and IGameService beans are properly created and injected by the Spring context.
    @Test
    void testBeanInitialization() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

            ICardService cardService = context.getBean(ICardService.class);
            assertNotNull(cardService, "CardService bean should not be null");

            IPlayerService playerService = context.getBean(IPlayerService.class);
            assertNotNull(playerService, "PlayerService bean should not be null");

            IGameService gameService = context.getBean(IGameService.class);
            assertNotNull(gameService, "GameService bean should not be null");

            assertSame(playerService, gameService.getPlayerService(), "GameService should use the same PlayerService bean");
        }
    }
}
