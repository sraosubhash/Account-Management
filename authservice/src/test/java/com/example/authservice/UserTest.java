package com.example.authservice;

import org.junit.jupiter.api.Test;

import com.example.authservice.model.User;
import com.example.authservice.model.UserRole;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class UserTest {

    @Test
    void testUserBuilder() {
        // Arrange: Create a user using the builder
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("securepassword")
                .mobile("1234567890")
                .role(UserRole.USER)
                .firstName("John")
                .lastName("Doe")
                .alternatePhone("0987654321")
                .address("123 Main St")
                .securityQuestion("What is your pet’s name?")
                .securityAnswer("Fluffy")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act & Assert: Verify the field values
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("securepassword", user.getPassword());
        assertEquals("1234567890", user.getMobile());
        assertEquals(UserRole.USER, user.getRole());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("0987654321", user.getAlternatePhone());
        assertEquals("123 Main St", user.getAddress());
        assertEquals("What is your pet’s name?", user.getSecurityQuestion());
        assertEquals("Fluffy", user.getSecurityAnswer());

        // Check timestamps are not null
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act: Create a user using the no-args constructor
        User user = new User();

        // Assert: Ensure the object is not null and has default values
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange: Create timestamps
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act: Create a user using the all-args constructor
        User user = new User(1L, "test@example.com", "securepassword", "1234567890",
                UserRole.USER, "John", "Doe", "0987654321", "123 Main St",
                "What is your pet’s name?", "Fluffy", createdAt, updatedAt);

        // Assert: Verify the field values
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("securepassword", user.getPassword());
        assertEquals("1234567890", user.getMobile());
        assertEquals(UserRole.USER, user.getRole());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("0987654321", user.getAlternatePhone());
        assertEquals("123 Main St", user.getAddress());
        assertEquals("What is your pet’s name?", user.getSecurityQuestion());
        assertEquals("Fluffy", user.getSecurityAnswer());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testToString() {
        // Arrange: Create a user
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("securepassword")
                .mobile("1234567890")
                .build();

        // Act: Convert to string
        String userString = user.toString();

        // Assert: Ensure it contains expected values
        assertTrue(userString.contains("test@example.com"));
        assertTrue(userString.contains("securepassword"));
        assertTrue(userString.contains("1234567890"));
    }
}
