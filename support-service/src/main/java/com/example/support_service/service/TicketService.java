package com.example.support_service.service;

import com.example.support_service.client.AuthServiceClient;
import com.example.support_service.dto.TicketRequest;
import com.example.support_service.dto.TicketResponse;
import com.example.support_service.dto.TicketStatusUpdate;
import com.example.support_service.entity.Role;
import com.example.support_service.entity.Ticket;
import com.example.support_service.entity.TicketStatus;
import com.example.support_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final AuthServiceClient authServiceClient;

    public TicketResponse createTicket(TicketRequest request) {
        checkIfUserExists(request.getUserId(), Role.USER.toString());
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setUserId(request.getUserId());
        ticket.setStatus(TicketStatus.NEW);

        return mapToResponse(ticketRepository.save(ticket));
    }

    public void checkIfUserExists(Long userId, String role) {
        ResponseEntity<Boolean> userExists = authServiceClient.validateRole(userId, role);
        if (Boolean.FALSE.equals(userExists.getBody())) {
            throw new EntityNotFoundException(role + " not found with Id: " + userId);
        }
    }

    public TicketResponse assignTicket(Long ticketId, Long employeeId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        checkIfUserExists(employeeId, Role.EMPLOYEE.toString());
        ticket.setEmployeeId(employeeId);
        ticket.setStatus(TicketStatus.ASSIGNED);

        return mapToResponse(ticketRepository.save(ticket));
    }

    public TicketResponse updateTicketStatus(Long ticketId, TicketStatusUpdate statusUpdate) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        ticket.setStatus(statusUpdate.getStatus());

        return mapToResponse(ticketRepository.save(ticket));
    }

    public List<TicketResponse> getUserTickets(Long userId) {
        checkIfUserExists(userId, Role.USER.toString());
        return ticketRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList(); 
    }

    public List<TicketResponse> getEmployeeTickets(Long employeeId) {
        checkIfUserExists(employeeId, Role.EMPLOYEE.toString());
        return ticketRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .toList(); 
    }

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList(); 
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setUserId(ticket.getUserId());
        response.setEmployeeId(ticket.getEmployeeId());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        return response;
    }
}