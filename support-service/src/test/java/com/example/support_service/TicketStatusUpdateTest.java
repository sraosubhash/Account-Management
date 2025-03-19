package com.example.support_service;

import com.example.support_service.dto.TicketStatusUpdate;
import com.example.support_service.entity.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketStatusUpdateTest {

    private TicketStatusUpdate ticketStatusUpdate;

    @BeforeEach
    void setUp() {
        ticketStatusUpdate = new TicketStatusUpdate();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(ticketStatusUpdate);
        assertNull(ticketStatusUpdate.getStatus(), "Status should be null initially");
    }

    @Test
    void testGetterAndSetter() {
        ticketStatusUpdate.setStatus(TicketStatus.IN_PROGRESS);
        assertEquals(TicketStatus.IN_PROGRESS, ticketStatusUpdate.getStatus(), "Status should be IN_PROGRESS");
        
        ticketStatusUpdate.setStatus(TicketStatus.RESOLVED);
        assertEquals(TicketStatus.RESOLVED, ticketStatusUpdate.getStatus(), "Status should be RESOLVED");
    }

    @Test
    void testToString() {
        ticketStatusUpdate.setStatus(TicketStatus.CLOSED);
        String toStringOutput = ticketStatusUpdate.toString();
        
        assertTrue(toStringOutput.contains("CLOSED"), "toString() should contain 'CLOSED'");
    }
}
