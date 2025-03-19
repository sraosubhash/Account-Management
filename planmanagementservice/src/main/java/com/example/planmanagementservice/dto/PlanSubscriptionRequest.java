package com.example.planmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Subhash part
public class PlanSubscriptionRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Plan ID is required")
    private String planId;
}