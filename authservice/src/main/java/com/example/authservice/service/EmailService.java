package com.example.authservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private  final Logger log = Logger.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // The sender email configured in properties
    private String senderEmail;

    public void sendEmail(String to, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: " + to, e);
        }
    }
}
