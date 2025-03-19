package com.example.planmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Integer dataLimit;
    private Integer smsLimit;
    private String talkTimeMinutes;
    private List<String> features;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
