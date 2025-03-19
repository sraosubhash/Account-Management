package com.example.authservice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.authservice.dto.LoginRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("valid@example.com", "9999999999", "StrongPassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "No validation errors should occur for a valid LoginRequest.");
    }
    
    @Test
    void testBlankFields() {
        LoginRequest request = new LoginRequest("", "", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Validation should fail due to @NotBlank constraint");
    }


    @Test
    void testInvalidEmail() {
        LoginRequest request = new LoginRequest("invalid-email", "9999999999", "StrongPassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Validation should fail for an invalid email.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Email validation should trigger an error.");
    }

    
    
    @Test
    void testBlankPassword() {
        LoginRequest request = new LoginRequest("valid@example.com", "9999999999", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Validation should fail when password is blank.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")), "Password validation should trigger an error.");
    }
}
