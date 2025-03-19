package com.example.planmanagementservice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.dto.CreatePlanRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateplanRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidCreatePlanRequest() {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .name("Basic Plan")
                .description("A basic plan with limited features.")
                .price(BigDecimal.valueOf(99.99))
                .duration(30)
                .dataLimit(10)
                .smsLimit(100)
                .talkTimeMinutes("500")
                .features(List.of("Feature1", "Feature2"))
                .build();

        Set<ConstraintViolation<CreatePlanRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "There should be no validation errors for a valid request.");
    }

    @Test
    void testInvalidName() {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .name("A") // Invalid name, too short
                .description("Valid description")
                .price(BigDecimal.valueOf(100))
                .duration(30)
                .dataLimit(10)
                .smsLimit(100)
                .talkTimeMinutes("500")
                .features(List.of("Feature1"))
                .build();

        Set<ConstraintViolation<CreatePlanRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "There should be one validation error.");
        assertEquals("Plan name must be between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPrice() {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .name("Plan 1")
                .description("Valid description")
                .price(BigDecimal.valueOf(-10)) // Invalid price, negative value
                .duration(30)
                .dataLimit(10)
                .smsLimit(100)
                .talkTimeMinutes("500")
                .features(List.of("Feature1"))
                .build();

        Set<ConstraintViolation<CreatePlanRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "There should be one validation error.");
        assertEquals("Price must be greater than or equal to 0", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidDuration() {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .name("Plan 1")
                .description("Valid description")
                .price(BigDecimal.valueOf(100))
                .duration(0) // Invalid duration, less than 1
                .dataLimit(10)
                .smsLimit(100)
                .talkTimeMinutes("500")
                .features(List.of("Feature1"))
                .build();

        Set<ConstraintViolation<CreatePlanRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "There should be one validation error.");
        assertEquals("Duration must be at least 1 day", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidDataLimit() {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .name("Plan 1")
                .description("Valid description")
                .price(BigDecimal.valueOf(100))
                .duration(30)
                .dataLimit(0) // Invalid data limit, less than 1 GB
                .smsLimit(100)
                .talkTimeMinutes("500")
                .features(List.of("Feature1"))
                .build();

        Set<ConstraintViolation<CreatePlanRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "There should be one validation error.");
        assertEquals("Data limit must be at least 1 GB", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidFeatures() {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .name("Plan 1")
                .description("Valid description")
                .price(BigDecimal.valueOf(100))
                .duration(30)
                .dataLimit(10)
                .smsLimit(100)
                .talkTimeMinutes("500")
                .features(List.of()) // Invalid, empty feature list
                .build();

        Set<ConstraintViolation<CreatePlanRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size(), "There should be one validation error.");
        assertEquals("At least one feature is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidActiveFlag() {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .name("Plan 1")
                .description("Valid description")
                .price(BigDecimal.valueOf(100))
                .duration(30)
                .dataLimit(10)
                .smsLimit(100)
                .talkTimeMinutes("500")
                .features(List.of("Feature1"))
                .active(true) // Valid active flag
                .build();

        Set<ConstraintViolation<CreatePlanRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "There should be no validation errors with a valid active flag.");
    }
}
