package com.example.support_service;

import com.example.support_service.dto.TicketResponse;
import com.example.support_service.entity.TicketPriority;
import com.example.support_service.entity.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketResponseTest {

    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        ticketResponse = new TicketResponse();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(ticketResponse);
        assertNull(ticketResponse.getId());
        assertNull(ticketResponse.getTitle());
        assertNull(ticketResponse.getDescription());
        assertNull(ticketResponse.getStatus());
        assertNull(ticketResponse.getPriority());
        assertNull(ticketResponse.getUserId());
        assertNull(ticketResponse.getEmployeeId());
        assertNull(ticketResponse.getCreatedAt());
        assertNull(ticketResponse.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        TicketResponse response = new TicketResponse();
        response.setId(1L);
        response.setTitle("Issue with Login");
        response.setDescription("User cannot log in.");
        response.setStatus(TicketStatus.NEW);
        response.setPriority(TicketPriority.HIGH);
        response.setUserId(1001L);
        response.setEmployeeId(2002L);
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertEquals(1L, response.getId());
        assertEquals("Issue with Login", response.getTitle());
        assertEquals("User cannot log in.", response.getDescription());
        assertEquals(TicketStatus.NEW, response.getStatus());
        assertEquals(TicketPriority.HIGH, response.getPriority());
        assertEquals(1001L, response.getUserId());
        assertEquals(2002L, response.getEmployeeId());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testGetterAndSetter() {
        LocalDateTime now = LocalDateTime.now();

        ticketResponse.setId(2L);
        ticketResponse.setTitle("Payment Failure");
        ticketResponse.setDescription("User is unable to complete payment.");
        ticketResponse.setStatus(TicketStatus.IN_PROGRESS);
        ticketResponse.setPriority(TicketPriority.URGENT);
        ticketResponse.setUserId(3003L);
        ticketResponse.setEmployeeId(4004L);
        ticketResponse.setCreatedAt(now);
        ticketResponse.setUpdatedAt(now);

        assertEquals(2L, ticketResponse.getId());
        assertEquals("Payment Failure", ticketResponse.getTitle());
        assertEquals("User is unable to complete payment.", ticketResponse.getDescription());
        assertEquals(TicketStatus.IN_PROGRESS, ticketResponse.getStatus());
        assertEquals(TicketPriority.URGENT, ticketResponse.getPriority());
        assertEquals(3003L, ticketResponse.getUserId());
        assertEquals(4004L, ticketResponse.getEmployeeId());
        assertEquals(now, ticketResponse.getCreatedAt());
        assertEquals(now, ticketResponse.getUpdatedAt());
    }

    @Test
    void testToString() {
        ticketResponse.setId(3L);
        ticketResponse.setTitle("App Crash");
        ticketResponse.setDescription("The application crashes on startup.");
        ticketResponse.setStatus(TicketStatus.RESOLVED);
        ticketResponse.setPriority(TicketPriority.MEDIUM);
        ticketResponse.setUserId(5005L);
        ticketResponse.setEmployeeId(6006L);
        ticketResponse.setCreatedAt(LocalDateTime.now());
        ticketResponse.setUpdatedAt(LocalDateTime.now());

        String toStringOutput = ticketResponse.toString();
        assertTrue(toStringOutput.contains("App Crash"));
        assertTrue(toStringOutput.contains("The application crashes on startup."));
        assertTrue(toStringOutput.contains("RESOLVED"));
        assertTrue(toStringOutput.contains("MEDIUM"));
        assertTrue(toStringOutput.contains("5005"));
        assertTrue(toStringOutput.contains("6006"));
    }
}
