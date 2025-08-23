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
 * Spring configuration class for the TopCard application.
 * This class is a central source of bean definitions and is responsible for configuring the Spring IoC container.
 * The @ComponentScan annotation tells Spring to automatically discover and register components annotated with
 * stereotypes like @Component, @Service, and @Repository within the specified package.
 * The @Bean methods are used to explicitly define beans for the service interfaces.
 */
@Configuration
@ComponentScan("com.topcard")   // Scan entire project classes including sub-classes
public class AppConfig {

    /**
     * Defines a bean for the IGameService interface, wiring it to its concrete implementation, GameService.
     * @return a new instance of GameService
     */
    @Bean
    public IGameService gameService(IPlayerService playerService) {
        return new GameService(playerService);
    }

    /**
     * Defines a bean for the ICardService interface, wiring it to CardService.
     * @return a new instance of CardService
     */
    @Bean
    public ICardService cardService() {
        return new CardService();
    }

    /**
     * Defines a bean for the IPlayerService interface, wiring it to PlayerService.
     * @return a new instance of PlayerServiceImpl
     */
    @Bean
    public IPlayerService playerService(IPlayerDao playerDao) {
        return new PlayerService(playerDao);
    }
}