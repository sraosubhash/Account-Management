package com.example.planmanagementservice.mapper;

import com.example.planmanagementservice.dto.CreatePlanRequest;


import com.example.planmanagementservice.dto.PagedPlanResponse;
import com.example.planmanagementservice.dto.PlanResponse;
import com.example.planmanagementservice.model.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanMapper {

    public Plan toEntity(CreatePlanRequest request) {
        Plan plan = new Plan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setDuration(request.getDuration());
        plan.setDataLimit(request.getDataLimit());
        plan.setSmsLimit(request.getSmsLimit());
        plan.setTalkTimeMinutes(request.getTalkTimeMinutes());
        plan.setFeatures(new ArrayList<>(request.getFeatures()));
        plan.setActive(request.isActive());
        return plan;
    }

    
    public PlanResponse toResponse(Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .duration(plan.getDuration())
                .dataLimit(plan.getDataLimit())
                .smsLimit(plan.getSmsLimit())
                .talkTimeMinutes(plan.getTalkTimeMinutes())
                .features(new ArrayList<>(plan.getFeatures()))
                .active(plan.isActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    public PagedPlanResponse toPagedResponse(Page<Plan> planPage) {
        List<PlanResponse> plans = planPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PagedPlanResponse.builder()
                .plans(plans)
                .pageNumber(planPage.getNumber())
                .pageSize(planPage.getSize())
                .totalElements(planPage.getTotalElements())
                .totalPages(planPage.getTotalPages())
                .hasNext(planPage.hasNext())
                .hasPrevious(planPage.hasPrevious())
                .build();
    }
}