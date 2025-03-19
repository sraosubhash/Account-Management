package com.example.mock;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.paymentservice.client.AuthServiceClient;
import com.example.paymentservice.client.PlanManagementServiceClient;
import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.PaymentStatus;
import com.example.paymentservice.exception.ResourceNotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.PaymentService;

@ExtendWith(MockitoExtension.class)
 class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private PlanManagementServiceClient planManagementServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequest.builder()
                .userId(1L)
                .planId("101")
                .amount(BigDecimal.valueOf(100.00))
                .build();

        payment = Payment.builder()
                .userId(1L)
                .planId("101")
                .amount(BigDecimal.valueOf(100.00))
                .status(PaymentStatus.PENDING)
                .transactionId("TXN20250219001")
                .build();
    }

    @Test
    void testProcessPayment_Success() {
        // Arrange
        when(authServiceClient.validateRole(paymentRequest.getUserId(), "USER"))
                .thenReturn(ResponseEntity.ok(true));
        when(planManagementServiceClient.validatePlan(paymentRequest.getPlanId()))
                .thenReturn(ResponseEntity.ok(true));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.processPayment(paymentRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Payment processed successfully", response.getMessage());
        assertEquals("COMPLETED", response.getStatus());
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_UserNotFound() {
        // Arrange
        when(authServiceClient.validateRole(paymentRequest.getUserId(), "USER"))
                .thenReturn(ResponseEntity.ok(false));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> paymentService.processPayment(paymentRequest));
        
        assertEquals("User not found with ID: " + paymentRequest.getUserId(), exception.getMessage());
    }

    @Test
    void testProcessPayment_PlanNotFound() {
        // Arrange
        when(authServiceClient.validateRole(paymentRequest.getUserId(), "USER"))
                .thenReturn(ResponseEntity.ok(true));
        when(planManagementServiceClient.validatePlan(paymentRequest.getPlanId()))
                .thenReturn(ResponseEntity.ok(false));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> paymentService.processPayment(paymentRequest));
        
        assertEquals("Plan not found with ID: " + paymentRequest.getPlanId(), exception.getMessage());
    }

    @Test
    void testGetUserPayments() {
        // Arrange
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of(payment));

        // Act
        List<Payment> payments = paymentService.getUserPayments(1L);

        // Assert
        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals(payment.getUserId(), payments.get(0).getUserId());
    }

    @Test
    void testGetPaymentByTransactionId_Success() {
        // Arrange
        when(paymentRepository.findByTransactionId("TXN20250219001"))
                .thenReturn(Optional.of(payment));

        // Act
        Optional<Payment> result = paymentService.getPaymentByTransactionId("TXN20250219001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TXN20250219001", result.get().getTransactionId());
    }

    @Test
    void testGetPaymentByTransactionId_NotFound() {
        // Arrange
        when(paymentRepository.findByTransactionId("TXN20250219002"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Payment> result = paymentService.getPaymentByTransactionId("TXN20250219002");

        // Assert
        assertFalse(result.isPresent());
    }
}

