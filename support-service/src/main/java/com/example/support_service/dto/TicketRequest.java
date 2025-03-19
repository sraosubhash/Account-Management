package com.example.support_service.dto;

import com.example.support_service.entity.TicketPriority;
import lombok.Data;

@Data
public class TicketRequest {
    private String title;
    private String description;
    private TicketPriority priority;
    private Long userId;
}