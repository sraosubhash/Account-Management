package com.example.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PLAN-MANAGEMENT-SERVICE")
public interface PlanManagementServiceClient {

    @GetMapping("/plans/validate-plan/{id}")
    ResponseEntity<Boolean> validatePlan(@PathVariable String id);
}
