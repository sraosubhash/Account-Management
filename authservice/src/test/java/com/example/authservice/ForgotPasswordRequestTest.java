package com.example.authservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.authservice.dto.ForgotPasswordRequest;

import static org.junit.jupiter.api.Assertions.*;

class ForgotPasswordRequestTest {

    private ForgotPasswordRequest request;

    @BeforeEach
    void setUp() {
        request = new ForgotPasswordRequest();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(request);
        assertNull(request.getEmail());
        assertNull(request.getSecurityQuestion());
        assertNull(request.getSecurityAnswer());
    }

    @Test
    void testAllArgsConstructor() {
        ForgotPasswordRequest testRequest = new ForgotPasswordRequest( // ✅ Renamed to `testRequest`
                "user@example.com", "What is your pet's name?", "Fluffy"
        );

        assertEquals("user@example.com", testRequest.getEmail());
        assertEquals("What is your pet's name?", testRequest.getSecurityQuestion());
        assertEquals("Fluffy", testRequest.getSecurityAnswer());
    }


    @Test
    void testGettersAndSetters() {
        request.setEmail("test@example.com");
        request.setSecurityQuestion("Where were you born?");
        request.setSecurityAnswer("New York");

        assertEquals("test@example.com", request.getEmail());
        assertEquals("Where were you born?", request.getSecurityQuestion());
        assertEquals("New York", request.getSecurityAnswer());
    }

    @Test
    void testToString() {
        request.setEmail("test@example.com");
        request.setSecurityQuestion("Favorite color?");
        request.setSecurityAnswer("Blue");

        String toStringOutput = request.toString();
        assertTrue(toStringOutput.contains("test@example.com"));
        assertTrue(toStringOutput.contains("Favorite color?"));
        assertTrue(toStringOutput.contains("Blue"));
    }
}
