package com.example.mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.authservice.dto.*;
import com.example.authservice.exception.BadRequestException;
import com.example.authservice.exception.ResourceNotFoundException;
import com.example.authservice.model.User;
import com.example.authservice.model.UserRole;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.JwtTokenProvider;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String MOBILE = "1234567890";
    private static final Long USER_ID = 1L;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail(EMAIL);
        user.setMobile(MOBILE);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.USER);
        user.setId(USER_ID);
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD, MOBILE);
        Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwtToken");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD, MOBILE);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(EMAIL, PASSWORD, MOBILE, "John", "Doe", "What is your pet's name?", "Fluffy", UserRole.USER, null, "Mysuru");
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepository.existsByMobile(MOBILE)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(emailService, times(1)).sendEmail(eq(EMAIL), anyString(), anyString());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(EMAIL, PASSWORD, MOBILE, "John", "Doe", "What is your pet's name?", "Fluffy", UserRole.USER,"Mysuru",null);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testValidateRole_Success() {
        // Arrange
        String role = "USER";
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        boolean result = authService.validateRole(USER_ID, role);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateRole_Failure() {
        // Arrange
        String role = "ADMIN";
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        boolean result = authService.validateRole(USER_ID, role);

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateSecurityAnswer_Success() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest(EMAIL, "What is your pet's name?", "Fluffy");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // Act
        boolean result = authService.validateSecurityAnswer(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateSecurityAnswer_Failure() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest(EMAIL, "What is your pet's name?", "WrongAnswer");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // Act
        boolean result = authService.validateSecurityAnswer(request);

        // Assert
        assertFalse(result);
    }

    @Test
    void testResetPassword_Success() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest(EMAIL, "What is your pet's name?", "Fluffy", "newPassword");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        authService.resetPassword(request);

        // Assert
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void testResetPassword_InvalidSecurityAnswer() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest(EMAIL, "What is your pet's name?", "WrongAnswer", "newPassword");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.resetPassword(request));
    }

    @Test
    void testGetAllEmployees_Success() {
        // Arrange
        List<User> employees = new ArrayList<>();
        employees.add(user);
        when(userRepository.findByRole(UserRole.EMPLOYEE)).thenReturn(Optional.of(employees));

        // Act
        List<UserDTO> employeeDTOs = authService.getAllEmployees();

        // Assert
        assertEquals(1, employeeDTOs.size());
    }

    @Test
    void testFindUserById_Success() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        UserDTO userDTO = authService.findUserById(USER_ID);

        // Assert
        assertNotNull(userDTO);
        assertEquals(EMAIL, userDTO.getEmail());
    }

    @Test
    void testFindUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.findUserById(USER_ID));
    }
}
