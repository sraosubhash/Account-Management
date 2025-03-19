package com.example.planmanagementservice.controller;


import com.example.planmanagementservice.dto.*;
import com.example.planmanagementservice.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
	 private static final Logger log = Logger.getLogger(PlanController.class);

    private final PlanService planService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(
            @Valid @RequestBody CreatePlanRequest request) {
        log.info("Create plan");
        PlanResponse createdPlan = planService.createPlan(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plan created successfully", createdPlan));
    }



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlan(@PathVariable String id) {
        PlanResponse plan = planService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedPlanResponse>> getAllActivePlans(
            @PageableDefault(size = 20, sort = "price") Pageable pageable) {
        PagedPlanResponse plans = planService.getAllActivePlans(pageable);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }


    @GetMapping("/validate-plan/{id}")
    public ResponseEntity<Boolean> validatePlan(@PathVariable String id) {
        return ResponseEntity.ok(planService.validatePlan(id));
    }
}