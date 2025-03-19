package com.example.support_service.controller;

import com.example.support_service.dto.TicketRequest;
import com.example.support_service.dto.TicketResponse;
import com.example.support_service.dto.TicketStatusUpdate;
import com.example.support_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/support/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(request));
    }
    
    @PutMapping("/{ticketId}/assign/{employeeId}")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(ticketService.assignTicket(ticketId, employeeId));
    }
    
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestBody TicketStatusUpdate statusUpdate) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, statusUpdate));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponse>> getUserTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getUserTickets(userId));
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TicketResponse>> getEmployeeTickets(@PathVariable Long employeeId) {
        return ResponseEntity.ok(ticketService.getEmployeeTickets(employeeId));
    }

    @GetMapping("/get-all-tickets")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }
}