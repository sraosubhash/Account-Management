package com.example.planmanagementservice.controller;

import com.example.planmanagementservice.dto.ApiResponse;
import com.example.planmanagementservice.dto.PlanResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.service.AdminPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
public class AdminPlanController {

    private final AdminPlanService adminPlanService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<PlanResponse>>> getAllPlans(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PlanResponse> response = adminPlanService.getAllPlans(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<ApiResponse<Page<UserPlanResponse>>> getAllSubscriptions(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserPlanResponse> response = adminPlanService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{planId}/activate")
    public ResponseEntity<ApiResponse<PlanResponse>> activatePlan(
            @PathVariable String planId) {
        PlanResponse response = adminPlanService.activatePlan(planId);
        return ResponseEntity.ok(ApiResponse.success("Plan activated successfully", response));
    }

    @PostMapping("/{planId}/deactivate")
    public ResponseEntity<ApiResponse<PlanResponse>> deactivatePlan(
            @PathVariable String planId) {
        PlanResponse response = adminPlanService.deactivatePlan(planId);
        return ResponseEntity.ok(ApiResponse.success("Plan deactivated successfully", response));
    }

    @PostMapping("/update-statuses")
    public ResponseEntity<ApiResponse<Void>> updatePlanStatuses() {
        adminPlanService.updatePlanStatuses();
        return ResponseEntity.ok(ApiResponse.success("Plan statuses updated successfully", null));
    }
}
