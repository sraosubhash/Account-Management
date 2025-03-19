package com.example.planmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//Subhash part
public class PlanHistoryResponse {
    private String userId;
    private List<UserPlanResponse> planHistory;
    private int totalPlans;
}