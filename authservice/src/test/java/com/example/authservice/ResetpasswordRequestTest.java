package com.example.authservice;

import org.junit.jupiter.api.Test;

import com.example.authservice.dto.ResetPasswordRequest;

import static org.junit.jupiter.api.Assertions.*;

class ResetpasswordRequestTest {

    @Test
    void testBuilder() {
        // Arrange: Create an object using the builder
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("test@example.com")
                .securityQuestion("What is your pet's name?")
                .securityAnswer("Fluffy")
                .newPassword("NewSecurePassword123")
                .build();

        // Act & Assert: Verify the field values
        assertEquals("test@example.com", request.getEmail());
        assertEquals("What is your pet's name?", request.getSecurityQuestion());
        assertEquals("Fluffy", request.getSecurityAnswer());
        assertEquals("NewSecurePassword123", request.getNewPassword());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange: Create an object using the no-args constructor
        ResetPasswordRequest request = new ResetPasswordRequest();

        // Act: Set values using setters
        request.setEmail("user@example.com");
        request.setSecurityQuestion("Where were you born?");
        request.setSecurityAnswer("New York");
        request.setNewPassword("StrongPass456");

        // Assert: Verify the field values
        assertEquals("user@example.com", request.getEmail());
        assertEquals("Where were you born?", request.getSecurityQuestion());
        assertEquals("New York", request.getSecurityAnswer());
        assertEquals("StrongPass456", request.getNewPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange: Create an object using the all-args constructor
        ResetPasswordRequest request = new ResetPasswordRequest(
                "admin@example.com", "Your favorite color?", "Blue", "AdminPass789"
        );

        // Assert: Verify the field values
        assertEquals("admin@example.com", request.getEmail());
        assertEquals("Your favorite color?", request.getSecurityQuestion());
        assertEquals("Blue", request.getSecurityAnswer());
        assertEquals("AdminPass789", request.getNewPassword());
    }

    @Test
    void testToString() {
        // Arrange: Create an object
        ResetPasswordRequest request = new ResetPasswordRequest(
                "test@example.com", "What is your pet's name?", "Fluffy", "SecurePass123"
        );

        // Act: Convert to string
        String requestString = request.toString();

        // Assert: Ensure it contains expected values
        assertTrue(requestString.contains("test@example.com"));
        assertTrue(requestString.contains("Fluffy"));
        assertTrue(requestString.contains("SecurePass123"));
    }
    
    
    

}
