package com.example.paymentservice.service;

import com.example.paymentservice.client.AuthServiceClient;
import com.example.paymentservice.client.PlanManagementServiceClient;
import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.PaymentStatus;
import com.example.paymentservice.exception.ResourceNotFoundException;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuthServiceClient authServiceClient;
    private final PlanManagementServiceClient planManagementServiceClient;
    private static final String PREFIX = "TXN";
    private final AtomicInteger sequence = new AtomicInteger(1);


    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // First validate if user exists
        boolean userExists = Boolean.TRUE.equals(authServiceClient.validateRole(paymentRequest.getUserId(),"USER")
                .getBody());
        boolean planExists = Boolean.TRUE.equals(planManagementServiceClient.validatePlan(paymentRequest.getPlanId())
                .getBody());

        if (!userExists) {
            throw new ResourceNotFoundException("User not found with ID: " + paymentRequest.getUserId());
        }
        if (!planExists) {
            throw new ResourceNotFoundException("Plan not found with ID: " + paymentRequest.getPlanId());
        }

        // Continue with payment processing
        Payment payment = Payment.builder()
                .userId(paymentRequest.getUserId())
                .planId(paymentRequest.getPlanId())
                .amount(paymentRequest.getAmount())
                .status(PaymentStatus.PENDING)
                .transactionId(generateTransactionId())
                .build();

        payment = paymentRepository.save(payment);

        // Simulate payment processing
        payment.setStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus().toString())
                .message("Payment processed successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String generateTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq = String.format("%03d", sequence.getAndIncrement() % 1000);
        return PREFIX + date + seq;
    }

    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
}