package com.topcard.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {
        "com.topcard.network.authentication", // The AuthenticationServer
        "com.topcard.business",               // The PlayerManager is here
        "com.topcard.service.player",         // The PlayerService is here
        "com.topcard.dao.player"              // The Player DAO is here
    }, excludeFilters = @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            // exclusion list
            classes = {com.topcard.business.CardManager.class, com.topcard.business.GameManager.class}
    ))


public class AuthenticationServerConfig  {
}