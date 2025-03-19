package com.example.planmanagementservice.dto;

import com.example.planmanagementservice.model.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//Subhash part

public class UserPlanResponse {
    private String id;
    private String userId;
    private PlanResponse plan;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PlanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
