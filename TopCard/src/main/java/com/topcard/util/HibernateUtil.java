package com.topcard.util;

import com.topcard.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {

    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml") // Loads the configuration from hibernate.cfg.xml
                    .addAnnotatedClass(Player.class)        // Register entity classes programmatically
                    .buildSessionFactory();
            logger.info("Hibernate SessionFactory initialized successfully.");
        } catch (Throwable ex) {
            logger.fatal("Initial SessionFactory creation failed." + ex, ex);
            throw new ExceptionInInitializerError(ex);
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