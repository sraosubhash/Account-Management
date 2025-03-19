package com.example.mock;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.authservice.service.EmailService;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String senderEmail = "sender@example.com";

    private static final String TO = "recipient@example.com";
    private static final String SUBJECT = "Test Subject";
    private static final String MESSAGE = "Test Message";

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "senderEmail", senderEmail);
    }

    /**
     * Test for successful email sending
     */
    @Test
    void testSendEmail_Success() {
        // Arrange
        doNothing().when(mailSender).send(mimeMessage);

        // Act
        emailService.sendEmail(TO, SUBJECT, MESSAGE);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    /**
     * Test for MessagingException during mail sending
     */
    @Test
    void testSendEmail_MessagingException_In_Send() {
        // Arrange - Throw RuntimeException when sending email
        doThrow(new RuntimeException("Error sending email"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendEmail(TO, SUBJECT, MESSAGE);

        // Assert - Exception should be caught and logged
        verify(mailSender, times(1)).send(mimeMessage);
    }

    /**
     * Test for invalid email format
     */
    @Test
    void testSendEmail_InvalidEmailFormat() {
        // Act
        emailService.sendEmail("invalid-email", SUBJECT, MESSAGE);

        // Assert - Email should not be sent for invalid email format
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test for null email
     */
    @Test
    void testSendEmail_NullEmail() {
        // Act
        emailService.sendEmail(null, SUBJECT, MESSAGE);

        // Assert - Email should not be sent
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test for empty recipient email
     */
    @Test
    void testSendEmail_EmptyEmail() {
        // Act
        emailService.sendEmail("", SUBJECT, MESSAGE);

        // Assert - Email should not be sent
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
