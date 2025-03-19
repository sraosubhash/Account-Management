package com.example.planmanagementservice;

import com.example.planmanagementservice.dto.PlanHistoryResponse;
import com.example.planmanagementservice.dto.PlanResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.model.PlanStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanHistoryResponseTest {

    @Test
    void testConstructorAndGetters() {
        // Create sample UserPlanResponse list
        UserPlanResponse userPlanResponse1 = new UserPlanResponse("1", "user123", 
            new PlanResponse("1", "Plan1", "Description1", BigDecimal.valueOf(100), 30, 50, 100," 200", Arrays.asList("Feature1", "Feature2"), true, LocalDateTime.now(), LocalDateTime.now()), 
            LocalDateTime.now(), LocalDateTime.now().plusDays(30), PlanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        UserPlanResponse userPlanResponse2 = new UserPlanResponse("2", "user123", 
            new PlanResponse("2", "Plan2", "Description2", BigDecimal.valueOf(200), 60, 100, 200," 400", Arrays.asList("Feature3", "Feature4"), true, LocalDateTime.now(), LocalDateTime.now()), 
            LocalDateTime.now(), LocalDateTime.now().plusDays(60), PlanStatus.EXPIRED, LocalDateTime.now(), LocalDateTime.now());

        List<UserPlanResponse> planHistory = Arrays.asList(userPlanResponse1, userPlanResponse2);

        // Create PlanHistoryResponse object using the constructor
        PlanHistoryResponse planHistoryResponse = new PlanHistoryResponse("user123", planHistory, planHistory.size());

        // Verify constructor and getters
        assertEquals("user123", planHistoryResponse.getUserId(), "User ID should be 'user123'");
        assertEquals(2, planHistoryResponse.getPlanHistory().size(), "Plan history should contain 2 plans");
        assertEquals(2, planHistoryResponse.getTotalPlans(), "Total plans should be 2");
    }

    @Test
    void testSetters() {
        // Create an empty PlanHistoryResponse object
        PlanHistoryResponse planHistoryResponse = new PlanHistoryResponse();

        // Set values using setters
        UserPlanResponse userPlanResponse = new UserPlanResponse("3", "user456", 
            new PlanResponse("3", "Plan3", "Description3", BigDecimal.valueOf(300), 90, 150, 300, "500", Arrays.asList("Feature5", "Feature6"), true, LocalDateTime.now(), LocalDateTime.now()), 
            LocalDateTime.now(), LocalDateTime.now().plusDays(90), PlanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        
        planHistoryResponse.setUserId("user456");
        planHistoryResponse.setPlanHistory(Arrays.asList(userPlanResponse));
        planHistoryResponse.setTotalPlans(planHistoryResponse.getPlanHistory().size());

        // Verify values after using setters
        assertEquals("user456", planHistoryResponse.getUserId(), "User ID should be 'user456'");
        assertEquals(1, planHistoryResponse.getPlanHistory().size(), "Plan history should contain 1 plan");
        assertEquals(1, planHistoryResponse.getTotalPlans(), "Total plans should be 1");
    }

    @Test
    void testEmptyPlanHistoryResponse() {
        // Create an empty PlanHistoryResponse object
        PlanHistoryResponse planHistoryResponse = new PlanHistoryResponse();

        // Verify the default values of the empty object
        assertNull(planHistoryResponse.getUserId(), "User ID should be null by default");
        assertNull(planHistoryResponse.getPlanHistory(), "Plan history should be null by default");
        assertEquals(0, planHistoryResponse.getTotalPlans(), "Total plans should be 0 by default");
    }

    @Test
    void testToString() {
        // Create sample UserPlanResponse list
        UserPlanResponse userPlanResponse1 = new UserPlanResponse("1", "user123", 
            new PlanResponse("1", "Plan1", "Description1", BigDecimal.valueOf(100), 30, 50, 100," 200", Arrays.asList("Feature1", "Feature2"), true, LocalDateTime.now(), LocalDateTime.now()), 
            LocalDateTime.now(), LocalDateTime.now().plusDays(30), PlanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        UserPlanResponse userPlanResponse2 = new UserPlanResponse("2", "user123", 
            new PlanResponse("2", "Plan2", "Description2", BigDecimal.valueOf(200), 60, 100, 200, "400", Arrays.asList("Feature3", "Feature4"), true, LocalDateTime.now(), LocalDateTime.now()), 
            LocalDateTime.now(), LocalDateTime.now().plusDays(60), PlanStatus.EXPIRED, LocalDateTime.now(), LocalDateTime.now());

        List<UserPlanResponse> planHistory = Arrays.asList(userPlanResponse1, userPlanResponse2);

        // Create PlanHistoryResponse object
        PlanHistoryResponse planHistoryResponse = new PlanHistoryResponse("user123", planHistory, planHistory.size());

        // Get the toString representation
        String result = planHistoryResponse.toString();

        // Assert that the toString contains relevant information
        assertTrue(result.contains("user123"), "ToString should contain the user ID.");
        assertTrue(result.contains("Plan1"), "ToString should contain the name of the first plan.");
        assertTrue(result.contains("ACTIVE"), "ToString should contain the status of the first plan.");
    }
}
