package com.topcard.config;

import com.topcard.dao.player.IPlayerDao;
import com.topcard.service.card.CardService;
import com.topcard.service.card.ICardService;
import com.topcard.service.game.GameService;
import com.topcard.service.game.IGameService;
import com.topcard.service.player.IPlayerService;
import com.topcard.service.player.PlayerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Test configuration mimicking AppConfig to wire service beans during testing.
 */
@Configuration
@ComponentScan("com.topcard")
public class SpringAppConfigForTest {
    @Bean
    public IGameService gameService(IPlayerService playerService) {
        return new GameService(playerService);
    }

    @Bean
    public ICardService cardService() {
        return new CardService();
    }

    @Bean
    public IPlayerService playerService(IPlayerDao playerDao) {
        return new PlayerService(playerDao);
    }
}
