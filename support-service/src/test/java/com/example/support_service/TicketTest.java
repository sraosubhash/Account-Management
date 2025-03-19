package com.example.support_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.support_service.entity.Ticket;
import com.example.support_service.entity.TicketPriority;
import com.example.support_service.entity.TicketStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(ticket);
        assertNull(ticket.getId());
        assertNull(ticket.getTitle());
        assertNull(ticket.getDescription());
        assertNull(ticket.getStatus());
        assertNull(ticket.getPriority());
        assertNull(ticket.getUserId());
        assertNull(ticket.getEmployeeId());
        assertNull(ticket.getCreatedAt());
        assertNull(ticket.getUpdatedAt());
    }

    @Test
    void testGettersAndSetters() {
        ticket.setId(1L);
        ticket.setTitle("Login Issue");
        ticket.setDescription("User unable to login due to 403 error");
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setUserId(1001L);
        ticket.setEmployeeId(2001L);
        LocalDateTime now = LocalDateTime.now();
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);

        assertEquals(1L, ticket.getId());
        assertEquals("Login Issue", ticket.getTitle());
        assertEquals("User unable to login due to 403 error", ticket.getDescription());
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
        assertEquals(TicketPriority.HIGH, ticket.getPriority());
        assertEquals(1001L, ticket.getUserId());
        assertEquals(2001L, ticket.getEmployeeId());
        assertEquals(now, ticket.getCreatedAt());
        assertEquals(now, ticket.getUpdatedAt());
    }

    @Test
    void testPrePersist() {
        ticket.onCreate();

        assertNotNull(ticket.getCreatedAt(), "createdAt should not be null after onCreate()");
        assertNotNull(ticket.getUpdatedAt(), "updatedAt should not be null after onCreate()");
        assertEquals(ticket.getCreatedAt(), ticket.getUpdatedAt(), "createdAt and updatedAt should be the same on creation");
    }

    @Test
    void testPreUpdate() {
        ticket.onUpdate();

        assertNotNull(ticket.getUpdatedAt(), "updatedAt should not be null after onUpdate()");
    }
}
