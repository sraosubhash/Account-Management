package com.example.planmanagementservice.controller;

import com.example.planmanagementservice.dto.PlanSubscriptionRequest;
import com.example.planmanagementservice.dto.ApiResponse;
import com.example.planmanagementservice.dto.PlanHistoryResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.dto.PlanUsageDTO;
import com.example.planmanagementservice.service.UserPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-plans")
@RequiredArgsConstructor
//Subhash part
public class UserPlanController {

    private final UserPlanService userPlanService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<UserPlanResponse>> subscribeToPlan(
            @Valid @RequestBody PlanSubscriptionRequest request) {
        UserPlanResponse response = userPlanService.subscribeToPlan(
                request.getUserId(),
                request.getPlanId()
        );
        return ResponseEntity.ok(ApiResponse.success("Plan subscription successful", response));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<ApiResponse<PlanHistoryResponse>> getUserPlanHistory(
            @PathVariable String userId) {
        PlanHistoryResponse response = userPlanService.getUserPlanHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<UserPlanResponse>> getActiveUserPlan(
            @PathVariable String userId) {
        UserPlanResponse response = userPlanService.getActiveUserPlan(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/usage")
    public ResponseEntity<ApiResponse<PlanUsageDTO>> getPlanUsage(
            @PathVariable String userId) {
        PlanUsageDTO response = userPlanService.getPlanUsage(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @PathVariable String subscriptionId) {
        userPlanService.cancelSubscription(subscriptionId);
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully", null));
    }
}