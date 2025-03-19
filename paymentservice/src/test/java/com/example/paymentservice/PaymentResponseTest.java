package com.example.paymentservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.paymentservice.dto.PaymentResponse;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PaymentResponseTest {
    private PaymentResponse paymentResponse;
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        timestamp = LocalDateTime.now();
        paymentResponse = new PaymentResponse("TXN12345", "SUCCESS", "Payment successful", timestamp);
    }

    @Test
    void testGettersAndSetters() {
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId("TXN67890");
        response.setStatus("FAILED");
        response.setMessage("Payment failed");
        response.setTimestamp(timestamp);

        assertEquals("TXN67890", response.getTransactionId());
        assertEquals("FAILED", response.getStatus());
        assertEquals("Payment failed", response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void testBuilderPattern() {
        PaymentResponse response = PaymentResponse.builder()
                .transactionId("TXN98765")
                .status("PENDING")
                .message("Processing payment")
                .timestamp(timestamp)
                .build();

        assertEquals("TXN98765", response.getTransactionId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("Processing payment", response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void testToString() {
        String result = paymentResponse.toString();
        assertTrue(result.contains("TXN12345"));
        assertTrue(result.contains("SUCCESS"));
        assertTrue(result.contains("Payment successful"));
        assertTrue(result.contains(timestamp.toString()));
    }

    @Test
    void testEqualsAndHashCode() {
        PaymentResponse response1 = new PaymentResponse("TXN12345", "SUCCESS", "Payment successful", timestamp);
        PaymentResponse response2 = new PaymentResponse("TXN12345", "SUCCESS", "Payment successful", timestamp);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}
