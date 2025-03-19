package com.example.support_service.dto;

import com.example.support_service.entity.TicketStatus;
import lombok.Data;

@Data
public class TicketStatusUpdate {
    private TicketStatus status;
}