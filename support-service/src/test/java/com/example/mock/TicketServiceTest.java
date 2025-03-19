package com.example.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.support_service.client.AuthServiceClient;
import com.example.support_service.dto.TicketRequest;
import com.example.support_service.dto.TicketResponse;
import com.example.support_service.dto.TicketStatusUpdate;
import com.example.support_service.entity.Role;
import com.example.support_service.entity.Ticket;
import com.example.support_service.entity.TicketPriority;
import com.example.support_service.entity.TicketStatus;
import com.example.support_service.repository.TicketRepository;
import com.example.support_service.service.TicketService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
 class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;
    private TicketRequest ticketRequest;
    private TicketStatusUpdate statusUpdate;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setUserId(100L);
        ticket.setStatus(TicketStatus.NEW);

        ticketRequest = new TicketRequest();
        ticketRequest.setTitle("Test Ticket");
        ticketRequest.setDescription("Test Description");
        ticketRequest.setPriority(TicketPriority.MEDIUM);
        ticketRequest.setUserId(100L);

        statusUpdate = new TicketStatusUpdate();
        statusUpdate.setStatus(TicketStatus.IN_PROGRESS);
    }

    /**
     * Test for createTicket method
     */
    @Test
    void testCreateTicket_Success() {
        // Arrange
        when(authServiceClient.validateRole(anyLong(), eq(Role.USER.toString())))
                .thenReturn(ResponseEntity.ok(true));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act
        TicketResponse response = ticketService.createTicket(ticketRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Test Ticket", response.getTitle());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    /**
     * Test for createTicket when user is not found
     */
    @Test
    void testCreateTicket_UserNotFound() {
        // Arrange
        when(authServiceClient.validateRole(anyLong(), eq(Role.USER.toString())))
                .thenReturn(ResponseEntity.ok(false));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> ticketService.createTicket(ticketRequest));

        assertEquals("USER not found with Id: 100", exception.getMessage());
    }

    /**
     * Test for assignTicket method
     */
    @Test
    void testAssignTicket_Success() {
        // Arrange
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));
        when(authServiceClient.validateRole(anyLong(), eq(Role.EMPLOYEE.toString())))
                .thenReturn(ResponseEntity.ok(true));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act
        TicketResponse response = ticketService.assignTicket(1L, 200L);

        // Assert
        assertNotNull(response);
        assertEquals(TicketStatus.ASSIGNED, response.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    /**
     * Test for assignTicket when ticket is not found
     */
    @Test
    void testAssignTicket_TicketNotFound() {
        // Arrange
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> ticketService.assignTicket(1L, 200L));

        assertEquals("Ticket not found", exception.getMessage());
    }

    /**
     * Test for updateTicketStatus method
     */
    @Test
    void testUpdateTicketStatus_Success() {
        // Arrange
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act
        TicketResponse response = ticketService.updateTicketStatus(1L, statusUpdate);

        // Assert
        assertNotNull(response);
        assertEquals(TicketStatus.IN_PROGRESS, response.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    /**
     * Test for updateTicketStatus when ticket is not found
     */
    @Test
    void testUpdateTicketStatus_TicketNotFound() {
        // Arrange
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> ticketService.updateTicketStatus(1L, statusUpdate));

        assertEquals("Ticket not found", exception.getMessage());
    }

    /**
     * Test for getUserTickets method
     */
    @Test
    void testGetUserTickets_Success() {
        // Arrange
        when(authServiceClient.validateRole(anyLong(), eq(Role.USER.toString())))
                .thenReturn(ResponseEntity.ok(true));
        when(ticketRepository.findByUserId(anyLong())).thenReturn(List.of(ticket));

        // Act
        List<TicketResponse> responses = ticketService.getUserTickets(100L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Ticket", responses.get(0).getTitle());
    }

    /**
     * Test for getAllTickets method
     */
    @Test
    void testGetAllTickets_Success() {
        // Arrange
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket));

        // Act
        List<TicketResponse> responses = ticketService.getAllTickets();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Ticket", responses.get(0).getTitle());
    }

    /**
     * Test for getEmployeeTickets method
     */
    @Test
    void testGetEmployeeTickets_Success() {
        // Arrange
        when(authServiceClient.validateRole(anyLong(), eq(Role.EMPLOYEE.toString())))
                .thenReturn(ResponseEntity.ok(true));
        when(ticketRepository.findByEmployeeId(anyLong())).thenReturn(List.of(ticket));

        // Act
        List<TicketResponse> responses = ticketService.getEmployeeTickets(200L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Ticket", responses.get(0).getTitle());
    }
}
