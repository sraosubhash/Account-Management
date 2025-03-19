package com.example.authservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.UserDTO;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    private AuthResponse authResponse;
    private UserDTO user;

    @BeforeEach
    void setUp() {
        // Arrange: Initialize UserDTO
        user = new UserDTO(1L, "9999999999", "test@example.com", "John", "Doe", null, "AlternatePhone", "Some Address");

        // Arrange: Initialize AuthResponse
        authResponse = new AuthResponse();
        authResponse.setToken("test-token-123");
        authResponse.setUser(user);
    }

    @Test
    void testGetters() {
        // Assert: Check if getters return expected values
        assertEquals("test-token-123", authResponse.getToken());
        assertEquals(user, authResponse.getUser());
        assertEquals("test@example.com", authResponse.getUser().getEmail());
        assertEquals("John", authResponse.getUser().getFirstName());
        assertEquals("Doe", authResponse.getUser().getLastName());
        assertEquals("9999999999", authResponse.getUser().getMobile());
        assertEquals("AlternatePhone", authResponse.getUser().getAlternatePhone());
        assertEquals("Some Address", authResponse.getUser().getAddress());
    }

    @Test
    void testSetters() {
        // Arrange: Create a new UserDTO
        UserDTO newUser = new UserDTO(2L, "8888888888", "new@example.com", "Jane", "Smith", null, "AltPhone", "Another Address");

        // Act: Modify values using setters
        authResponse.setToken("updated-token-456");
        authResponse.setUser(newUser);

        // Assert: Verify updated values
        assertEquals("updated-token-456", authResponse.getToken());
        assertEquals(newUser, authResponse.getUser());
        assertEquals("new@example.com", authResponse.getUser().getEmail());
        assertEquals("Jane", authResponse.getUser().getFirstName());
        assertEquals("Smith", authResponse.getUser().getLastName());
        assertEquals("8888888888", authResponse.getUser().getMobile());
        assertEquals("AltPhone", authResponse.getUser().getAlternatePhone());
        assertEquals("Another Address", authResponse.getUser().getAddress());
    }

    @Test
    void testNullValues() {
        // Act: Set null values
        authResponse.setToken(null);
        authResponse.setUser(null);

        // Assert: Ensure null values are handled correctly
        assertNull(authResponse.getToken());
        assertNull(authResponse.getUser());
    }

    @Test
    void testToString() {
        // Act: Convert object to string
        String responseString = authResponse.toString();

        // Assert: Verify the string contains expected values
        assertTrue(responseString.contains("test-token-123"));
        assertTrue(responseString.contains("test@example.com"));
        assertTrue(responseString.contains("John"));
        assertTrue(responseString.contains("Doe"));
    }
}
