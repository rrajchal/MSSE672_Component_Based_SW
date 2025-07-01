package com.topcard.debug;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class tests displays of messages in different colors
 */
class DebugTest {

    @Test
    void testSetDebugMode() {
        Debug.setDebugMode(true);
        assertTrue(getDebugMode());
        Debug.setDebugMode(false);
        assertFalse(getDebugMode());
    }

    @Test
    void testInfo() {
        Debug.setDebugMode(true);
        Debug.info("This is a debug message.");
    }

    @Test
    void testWarn() {
        Debug.setDebugMode(true);
        Debug.info("Before warn: Text in default color.");
        Debug.warn("This is a warning message.");
        Debug.info("After warn: Text in default color.");

    }

    @Test
    void testError() {
        Debug.setDebugMode(true);
        Debug.info("Before warn: Text in default color.");
        Debug.error("This is an error message.");
        Debug.info("After warn: Text in default color.");
    }

    // Helper method to access the private debugMode field
    private boolean getDebugMode() {
        try {
            java.lang.reflect.Field field = Debug.class.getDeclaredField("debugMode");
            field.setAccessible(true);
            return field.getBoolean(null);
        } catch (Exception e) {
            fail("Failed to access debugMode field.");
            return false;
        }
    }
}
