package com.example.paymentservice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidPaymentRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setPlanId("PLAN_123");
        request.setAmount(BigDecimal.valueOf(100.00));

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "No validation errors should occur for a valid request.");
    }

    @Test
    void testNullUserId() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(null);
        request.setPlanId("PLAN_123");
        request.setAmount(BigDecimal.valueOf(100.00));

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "User ID should not be null.");
    }

    @Test
    void testNullPlanId() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setPlanId(null);
        request.setAmount(BigDecimal.valueOf(100.00));

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Plan ID should not be null.");
    }

    @Test
    void testNullAmount() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setPlanId("PLAN_123");
        request.setAmount(null);

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Amount should not be null.");
    }

    @Test
    void testNegativeAmount() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setPlanId("PLAN_123");
        request.setAmount(BigDecimal.valueOf(-50.00));

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Amount must be positive.");
    }

    @Test
    void testZeroAmount() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setPlanId("PLAN_123");
        request.setAmount(BigDecimal.ZERO);

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Amount must be greater than zero.");
    }

    @Test
    void testToString() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setPlanId("PLAN_123");
        request.setAmount(BigDecimal.valueOf(200.00));

        String result = request.toString();
        
        System.out.println("Generated toString(): " + result);  // Print to debug

        assertTrue(result.contains("PLAN_123"), "ToString should contain the plan ID.");
        assertTrue(result.contains("amount=200"), "ToString should contain the correct amount."); // Changed assertion
    }


}
