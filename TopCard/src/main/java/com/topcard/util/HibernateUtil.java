package com.topcard.util;

import com.topcard.domain.Player;
import com.topcard.exceptions.TopCardException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class HibernateUtil {

    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Load database settings from config.properties
            Properties props = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new RuntimeException("Missing config.properties file in resources.");
                }
                props.load(input);
            }

            // Configure Hibernate programmatically
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.url", props.getProperty("db.url"));
            configuration.setProperty("hibernate.connection.username", props.getProperty("db.username"));
            configuration.setProperty("hibernate.connection.password", props.getProperty("db.password"));
            configuration.setProperty("hibernate.dialect", props.getProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"));
            configuration.setProperty("hibernate.hbm2ddl.auto", props.getProperty("hibernate.hbm2ddl.auto", "validate"));
            configuration.setProperty("hibernate.show_sql", props.getProperty("hibernate.show_sql", "false"));

            // Add annotated entities
            configuration.addAnnotatedClass(Player.class);

            sessionFactory = configuration.buildSessionFactory();
            logger.info("Hibernate SessionFactory successfully initialized.");
        } catch (Exception e) {
            logger.fatal("Failed to initialize Hibernate SessionFactory.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory shut down successfully.");
        }
    }
}