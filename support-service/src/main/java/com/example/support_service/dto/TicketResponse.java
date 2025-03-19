package com.example.support_service.dto;

import com.example.support_service.entity.TicketPriority;
import com.example.support_service.entity.TicketStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long userId;
    private Long employeeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}