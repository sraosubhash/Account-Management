package com.example.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.authservice.exception.ResourceNotFoundException;
import com.example.authservice.model.User;
import com.example.authservice.model.UserRole;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.CustomUserDetailsService;
import com.example.authservice.service.UserPrincipal;

@ExtendWith(MockitoExtension.class)
 class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .mobile("1234567890")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();
    }

    /**
     * Test loadUserByUsername with valid email
     */
    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        UserPrincipal userDetails = (UserPrincipal) customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    /**
     * Test loadUserByUsername with invalid email
     */
    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
                () -> customUserDetailsService.loadUserByUsername("invalid@example.com"));
        
        assertEquals("User not found with email : invalid@example.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("invalid@example.com");
    }

    /**
     * Test loadUserById with valid ID
     */
    @Test
    void testLoadUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserPrincipal userDetails = (UserPrincipal) customUserDetailsService.loadUserById(1L);

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Test loadUserById with invalid ID
     */
    @Test
    void testLoadUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> customUserDetailsService.loadUserById(99L));
        
        assertTrue(exception.getMessage().contains("User not found with id"));
        verify(userRepository, times(1)).findById(99L);
    }
}
