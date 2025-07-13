package com.topcard.util;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUtilTest {

    @Test
    void testInitializeSession() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        assertNotNull(factory, "SessionFactory should be initialized");
    }

    @Test
    void testShutdownSession() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        HibernateUtil.shutdown();
        assertTrue(factory.isClosed(), "SessionFactory should be closed after shutdown");
    }

    @Test
    void testConfigProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            assertNotNull(input, "config.properties file should exist");
            props.load(input);
        }
        assertEquals("jdbc:mysql://localhost:3306/topcardDb", props.getProperty("db.url"));
        assertEquals("root", props.getProperty("db.username"));
        assertEquals("root", props.getProperty("db.password"));
    }
}
