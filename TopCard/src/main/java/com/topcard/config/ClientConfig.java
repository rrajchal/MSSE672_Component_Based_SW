package com.topcard.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.topcard.network.game",
    "com.topcard.presentation.common",
    "com.topcard.presentation.controller",
    "com.topcard.presentation.view",
    "com.topcard.dao.player"
})
public class ClientConfig {
}