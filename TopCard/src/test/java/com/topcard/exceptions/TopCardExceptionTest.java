package com.topcard.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import com.topcard.exceptions.TopCardException;

public class TopCardExceptionTest {

    @Test
    public void testMessageConstructor() {
        String message = "Test exception message";
        TopCardException exception = new TopCardException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testMessageAndCauseConstructor() {
        String message = "Test exception message with cause";
        Throwable cause = new RuntimeException("Cause of the exception");
        TopCardException exception = new TopCardException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testCauseConstructor() {
        Throwable cause = new RuntimeException("Cause of the exception");
        TopCardException exception = new TopCardException(cause);
        assertEquals(cause, exception.getCause());
    }
}
