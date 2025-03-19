package com.example.authservice;

import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.model.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "9999999999", "SecurePass1",
                "John", "Doe", "8888888888", "123 Street",
                UserRole.EMPLOYEE, "What is your pet's name?", "Fluffy"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "There should be no validation errors for a valid request.");
    }

    @Test
    void testInvalidEmail() {
        RegisterRequest request = new RegisterRequest(
                "invalid-email", "9999999999", "SecurePass1",
                "John", "Doe", "8888888888", "123 Street",
                UserRole.EMPLOYEE, "What is your pet's name?", "Fluffy"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Email should be invalid.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Email validation should fail.");
    }

    @Test
    void testBlankFieldsPass() {
        RegisterRequest request = new RegisterRequest(
                "valid@example.com", "9999999999", "StrongPass1",
                "John", "Doe", "8888888888", "123 Street",
                UserRole.EMPLOYEE, "What is your favorite color?", "Blue"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "All required fields are provided, so validation should pass.");
    }

    

    @Test
    void testInvalidMobileNumber() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "123", "SecurePass1",
                "John", "Doe", "8888888888", "123 Street",
                UserRole.EMPLOYEE, "What is your pet's name?", "Fluffy"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Mobile number should fail validation.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("mobile")), "Mobile number should have incorrect length.");
    }

    @Test
    void testInvalidPasswordLength() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "9999999999", "123",
                "John", "Doe", "8888888888", "123 Street",
                UserRole.EMPLOYEE, "What is your pet's name?", "Fluffy"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Password should fail due to short length.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")), "Password should not meet length constraints.");
    }

    @Test
    void testNullRoleShouldPass() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "9999999999", "SecurePass1",
                "John", "Doe", "8888888888", "123 Street",
                null, "What is your pet's name?", "Fluffy"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Role is optional and should not trigger a validation error.");
    }
}
