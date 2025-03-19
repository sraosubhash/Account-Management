package com.example.authservice;

import com.example.authservice.dto.UserDTO;
import com.example.authservice.model.UserRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testUserDTOBuilder() {
        // Arrange: Create a UserDTO using the builder
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .mobile("1234567890")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .alternatePhone("0987654321")
                .address("123 Main St")
                .build();

        // Act & Assert: Verify the field values
        assertEquals(1L, userDTO.getId());
        assertEquals("1234567890", userDTO.getMobile());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
        assertEquals(UserRole.USER, userDTO.getRole());
        assertEquals("0987654321", userDTO.getAlternatePhone());
        assertEquals("123 Main St", userDTO.getAddress());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act: Create a UserDTO using the no-args constructor
        UserDTO userDTO = new UserDTO();

        // Assert: Ensure the object is not null and has default values
        assertNotNull(userDTO);
        assertNull(userDTO.getId());
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getMobile());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange: Create a UserDTO using the all-args constructor
        UserDTO userDTO = new UserDTO(1L, "1234567890", "test@example.com", 
                                      "John", "Doe", UserRole.USER, "0987654321", "123 Main St");

        // Assert: Verify the field values
        assertEquals(1L, userDTO.getId());
        assertEquals("1234567890", userDTO.getMobile());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
        assertEquals(UserRole.USER, userDTO.getRole());
        assertEquals("0987654321", userDTO.getAlternatePhone());
        assertEquals("123 Main St", userDTO.getAddress());
    }

    @Test
    void testToString() {
        // Arrange: Create a UserDTO
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .email("test@example.com")
                .mobile("1234567890")
                .build();

        // Act: Convert to string
        String dtoString = userDTO.toString();

        // Assert: Ensure it contains expected values
        assertTrue(dtoString.contains("test@example.com"));
        assertTrue(dtoString.contains("1234567890"));
    }
    

}
