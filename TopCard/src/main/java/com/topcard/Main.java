package com.topcard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// This is a test file and is not related to actual project
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Hello and welcome!");
        for (int i = 1; i <= 5; i++) {
            logger.debug("i = " + i);
        }
    }
}