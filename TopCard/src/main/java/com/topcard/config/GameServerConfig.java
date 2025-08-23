package com.topcard.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.topcard.network.game",      // GameServer, GameServerHandler
    "com.topcard.business",          // GameManager, PlayerManager
    "com.topcard.service.game",      // IGameService, GameService
    "com.topcard.service.player",    // PlayerService
    "com.topcard.dao.player",        // IPlayerDao implementation
    "com.topcard.service.card",      // ICardService, CardService
    "com.topcard.dao.card"           // ICardDao implementation
})
public class GameServerConfig {
}