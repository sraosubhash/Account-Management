package com.example.support_service;

import com.example.support_service.dto.TicketRequest;
import com.example.support_service.entity.TicketPriority;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketRequestTest {

    @Test
    void testTicketRequestGetterAndSetter() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTitle("System Crash");
        ticketRequest.setDescription("System crashes when clicking the submit button.");
        ticketRequest.setPriority(TicketPriority.HIGH);
        ticketRequest.setUserId(123L);

        assertThat(ticketRequest)
            .extracting("title", "description", "priority", "userId")
            .containsExactly("System Crash", "System crashes when clicking the submit button.", TicketPriority.HIGH, 123L);
    }

    @Test
    void testTicketRequestDefaultValues() {
        TicketRequest ticketRequest = new TicketRequest();

        assertThat(ticketRequest)
            .extracting("title", "description", "priority", "userId")
            .containsExactly(null, null, null, null);
    }

    @Test
    void testTicketRequestEqualsAndHashCode() {
        TicketRequest ticket1 = new TicketRequest();
        ticket1.setTitle("Issue 1");
        ticket1.setDescription("Some description");
        ticket1.setPriority(TicketPriority.MEDIUM);
        ticket1.setUserId(100L);

        TicketRequest ticket2 = new TicketRequest();
        ticket2.setTitle("Issue 1");
        ticket2.setDescription("Some description");
        ticket2.setPriority(TicketPriority.MEDIUM);
        ticket2.setUserId(100L);

        assertThat(ticket1).isEqualTo(ticket2).hasSameHashCodeAs(ticket2);
    }

    @Test
    void testTicketRequestNotEqual() {
        TicketRequest ticket1 = new TicketRequest();
        ticket1.setTitle("Issue A");
        ticket1.setDescription("Description A");
        ticket1.setPriority(TicketPriority.LOW);
        ticket1.setUserId(200L);

        TicketRequest ticket2 = new TicketRequest();
        ticket2.setTitle("Issue B");
        ticket2.setDescription("Description B");
        ticket2.setPriority(TicketPriority.HIGH);
        ticket2.setUserId(300L);

        assertThat(ticket1).isNotEqualTo(ticket2);
    }
    
    @Test
    void testNullTitleAndDescription() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTitle(null);
        ticketRequest.setDescription(null);

        assertThat(ticketRequest)
            .extracting("title", "description")
            .containsExactly(null, null);
    }

    @Test
    void testEmptyTitleAndDescription() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTitle("");
        ticketRequest.setDescription("");

        assertThat(ticketRequest)
            .extracting("title", "description")
            .containsExactly("", "");
    }

    @Test
    void testWhitespaceTitleAndDescription() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTitle("   ");
        ticketRequest.setDescription("   ");

        assertThat(ticketRequest)
            .extracting("title", "description")
            .containsExactly("   ", "   ");
    }

    @Test
    void testInvalidPriorityEnum() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TicketPriority.valueOf("INVALID_PRIORITY"); // This should throw an exception
        });

        assertThat(exception.getMessage()).contains("No enum constant com.example.support_service.entity.TicketPriority.INVALID_PRIORITY");
    }

    @Test
    void testNegativeUserId() {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setUserId(-1L);

        assertThat(ticketRequest.getUserId()).isLessThan(0);
    }

    @Test
    void testVeryLongTitleAndDescription() {
        TicketRequest ticketRequest = new TicketRequest();

        String longTitle = "A".repeat(500);
        String longDescription = "B".repeat(1000);

        ticketRequest.setTitle(longTitle);
        ticketRequest.setDescription(longDescription);

        assertThat(ticketRequest)
            .extracting("title", "description")
            .containsExactly(longTitle, longDescription);

        assertThat(ticketRequest.getTitle()).hasSize(500);
        assertThat(ticketRequest.getDescription()).hasSize(1000);
    }
}
