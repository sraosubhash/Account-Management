package com.example.planmanagementservice.mapper;

import com.example.planmanagementservice.dto.PlanHistoryResponse;

import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.model.UserPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class UserPlanMapper {
    private final PlanMapper planMapper;

    public UserPlanResponse toResponse(UserPlan userPlan) {
        return UserPlanResponse.builder()
                .id(userPlan.getId())
                .userId(userPlan.getUserId())
                .plan(planMapper.toResponse(userPlan.getPlan()))
                .startDate(userPlan.getStartDate())
                .endDate(userPlan.getEndDate())
                .status(userPlan.getStatus())
                .createdAt(userPlan.getCreatedAt())
                .updatedAt(userPlan.getUpdatedAt())
                .build();
    }

    public PlanHistoryResponse toHistoryResponse(String userId, List<UserPlan> userPlans) {
        List<UserPlanResponse> planHistory = userPlans.stream()
                .map(this::toResponse)
                .toList();

        return PlanHistoryResponse.builder()
                .userId(userId)
                .planHistory(planHistory)
                .totalPlans(planHistory.size())
                .build();
    }
}