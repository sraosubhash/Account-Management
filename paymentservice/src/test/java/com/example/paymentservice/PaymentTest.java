package com.example.paymentservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.PaymentStatus;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(payment);
        assertNull(payment.getId());
        assertNull(payment.getUserId());
        assertNull(payment.getPlanId());
        assertNull(payment.getAmount());
        assertNull(payment.getTransactionId());
        assertNull(payment.getStatus());
        assertNull(payment.getPaymentDate());
        assertNull(payment.getCreatedAt());
        assertNull(payment.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Payment newPayment = new Payment(  // Renamed variable 
                1L, 1001L, "PLAN123", BigDecimal.valueOf(99.99),
                "TXN12345", PaymentStatus.COMPLETED, now, now, now
        );

        assertEquals(1L, newPayment.getId());
        assertEquals(1001L, newPayment.getUserId());
        assertEquals("PLAN123", newPayment.getPlanId());
        assertEquals(BigDecimal.valueOf(99.99), newPayment.getAmount());
        assertEquals("TXN12345", newPayment.getTransactionId());
        assertEquals(PaymentStatus.COMPLETED, newPayment.getStatus());
        assertEquals(now, newPayment.getPaymentDate());
        assertEquals(now, newPayment.getCreatedAt());
        assertEquals(now, newPayment.getUpdatedAt());
    }


    @Test
    void testGettersAndSetters() {
        payment.setId(2L);
        payment.setUserId(2002L);
        payment.setPlanId("PLAN456");
        payment.setAmount(BigDecimal.valueOf(199.99));
        payment.setTransactionId("TXN67890");
        payment.setStatus(PaymentStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        payment.setPaymentDate(now);
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);

        assertEquals(2L, payment.getId());
        assertEquals(2002L, payment.getUserId());
        assertEquals("PLAN456", payment.getPlanId());
        assertEquals(BigDecimal.valueOf(199.99), payment.getAmount());
        assertEquals("TXN67890", payment.getTransactionId());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(now, payment.getPaymentDate());
        assertEquals(now, payment.getCreatedAt());
        assertEquals(now, payment.getUpdatedAt());
    }

    @Test
    void testPrePersist() throws Exception {
        // Using Reflection to invoke the protected method
        Method onCreateMethod = Payment.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(payment);

        assertNotNull(payment.getCreatedAt(), "createdAt should not be null after onCreate()");
        assertNotNull(payment.getPaymentDate(), "paymentDate should not be null after onCreate()");
        assertEquals(payment.getCreatedAt(), payment.getPaymentDate(), "createdAt and paymentDate should be the same on creation");
    }

    @Test
    void testPreUpdate() throws Exception {
        // Using Reflection to invoke the protected method
        Method onUpdateMethod = Payment.class.getDeclaredMethod("onUpdate");
        onUpdateMethod.setAccessible(true);
        onUpdateMethod.invoke(payment);

        assertNotNull(payment.getUpdatedAt(), "updatedAt should not be null after onUpdate()");
    }
}
