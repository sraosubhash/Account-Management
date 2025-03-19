package com.example.planmanagementservice;

import com.example.planmanagementservice.dto.PlanResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.model.PlanStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.*;

class UserPlanResponseTest {

    @Test
    void testConstructorAndGetters() {
        // Create PlanResponse for testing
        PlanResponse plan1 = new PlanResponse("1", "Plan1", "Description1", BigDecimal.valueOf(100), 30, 50, 100, "200", Arrays.asList("Feature1", "Feature2"), true, LocalDateTime.now(), LocalDateTime.now());

        // Create a UserPlanResponse with PlanStatus
        UserPlanResponse userPlanResponse1 = new UserPlanResponse(
            "1", 
            "user123", 
            plan1, 
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(30), 
            PlanStatus.ACTIVE,  // Using valid PlanStatus
            LocalDateTime.now(), 
            LocalDateTime.now()
        );

        // Verify the constructor and getters
        assertEquals("user123", userPlanResponse1.getUserId(), "User ID should be 'user123'");
        assertEquals(PlanStatus.ACTIVE, userPlanResponse1.getStatus(), "Plan status should be ACTIVE");
        assertNotNull(userPlanResponse1.getStartDate(), "Start date should not be null");
        assertNotNull(userPlanResponse1.getEndDate(), "End date should not be null");
    }

    @Test
    void testSetters() {
        // Create an empty UserPlanResponse object
        UserPlanResponse userPlanResponse = new UserPlanResponse();

        // Set values using setters
        PlanResponse plan2 = new PlanResponse("2", "Plan2", "Description2", BigDecimal.valueOf(200), 60, 100, 200, "400", Arrays.asList("Feature3", "Feature4"), true, LocalDateTime.now(), LocalDateTime.now());
        userPlanResponse.setUserId("user456");
        userPlanResponse.setPlan(plan2);
        userPlanResponse.setStatus(PlanStatus.EXPIRED);  // Setting status to EXPIRED
        userPlanResponse.setStartDate(LocalDateTime.now());
        userPlanResponse.setEndDate(LocalDateTime.now().plusDays(60));
        userPlanResponse.setCreatedAt(LocalDateTime.now());
        userPlanResponse.setUpdatedAt(LocalDateTime.now());

        // Verify values after using setters
        assertEquals("user456", userPlanResponse.getUserId(), "User ID should be 'user456'");
        assertEquals(PlanStatus.EXPIRED, userPlanResponse.getStatus(), "Plan status should be EXPIRED");
        assertNotNull(userPlanResponse.getStartDate(), "Start date should not be null");
        assertNotNull(userPlanResponse.getEndDate(), "End date should not be null");
    }

    @Test
    void testEmptyUserPlanResponse() {
        // Create an empty UserPlanResponse object
        UserPlanResponse userPlanResponse = new UserPlanResponse();

        // Verify the default values of an empty object
        assertNull(userPlanResponse.getUserId(), "User ID should be null by default");
        assertNull(userPlanResponse.getPlan(), "Plan should be null by default");
        assertNull(userPlanResponse.getStatus(), "Status should be null by default");
        assertNull(userPlanResponse.getStartDate(), "Start date should be null by default");
        assertNull(userPlanResponse.getEndDate(), "End date should be null by default");
    }
}
