package com.example.planmanagementservice;

import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.dto.PlanSubscriptionRequest;

import static org.junit.jupiter.api.Assertions.*;

class PlanSubscriptionRequestTest {

    @Test
    void testConstructorAndGetters() {
        // Create a PlanSubscriptionRequest object using the constructor
        PlanSubscriptionRequest request = new PlanSubscriptionRequest("user123", "plan456");

        // Verify the constructor and getters
        assertEquals("user123", request.getUserId(), "User ID should be 'user123'");
        assertEquals("plan456", request.getPlanId(), "Plan ID should be 'plan456'");
    }

    @Test
    void testSetters() {
        // Create an empty PlanSubscriptionRequest object
        PlanSubscriptionRequest request = new PlanSubscriptionRequest();

        // Set values using setters
        request.setUserId("user789");
        request.setPlanId("plan012");

        // Verify the values after using setters
        assertEquals("user789", request.getUserId(), "User ID should be 'user789'");
        assertEquals("plan012", request.getPlanId(), "Plan ID should be 'plan012'");
    }

    @Test
    void testToString() {
        // Create a PlanSubscriptionRequest object
        PlanSubscriptionRequest request = new PlanSubscriptionRequest("user123", "plan456");

        // Get the toString representation
        String result = request.toString();

        // Verify that toString contains userId and planId
        assertTrue(result.contains("user123"), "ToString should contain the user ID");
        assertTrue(result.contains("plan456"), "ToString should contain the plan ID");
    }
    
    @Test
    void testNullUserId() {
        // Create a PlanSubscriptionRequest object with null userId
        PlanSubscriptionRequest request = new PlanSubscriptionRequest();
        request.setUserId(null);
        request.setPlanId("plan456");

        // Verify that null value for userId is handled correctly (could lead to NPE or business logic rejection)
        assertNull(request.getUserId(), "User ID should be null.");
        assertEquals("plan456", request.getPlanId(), "Plan ID should still be 'plan456'");
    }

    @Test
    void testEmptyUserId() {
        // Create a PlanSubscriptionRequest object with empty userId
        PlanSubscriptionRequest request = new PlanSubscriptionRequest();
        request.setUserId("");  // Empty string for userId
        request.setPlanId("plan456");

        // Verify that empty userId is set (although it shouldn't be allowed based on the validation)
        assertEquals("", request.getUserId(), "User ID should be empty.");
        assertEquals("plan456", request.getPlanId(), "Plan ID should still be 'plan456'");
    }

    @Test
    void testNullPlanId() {
        // Create a PlanSubscriptionRequest object with null planId
        PlanSubscriptionRequest request = new PlanSubscriptionRequest();
        request.setUserId("user123");
        request.setPlanId(null);

        // Verify that null value for planId is handled correctly
        assertEquals("user123", request.getUserId(), "User ID should be 'user123'");
        assertNull(request.getPlanId(), "Plan ID should be null.");
    }

    @Test
    void testEmptyPlanId() {
        // Create a PlanSubscriptionRequest object with empty planId
        PlanSubscriptionRequest request = new PlanSubscriptionRequest();
        request.setUserId("user123");
        request.setPlanId("");  // Empty string for planId

        // Verify that empty planId is set (although it shouldn't be allowed based on the validation)
        assertEquals("user123", request.getUserId(), "User ID should be 'user123'");
        assertEquals("", request.getPlanId(), "Plan ID should be empty.");
    }

    @Test
    void testBothUserIdAndPlanIdNull() {
        // Create a PlanSubscriptionRequest object with both userId and planId as null
        PlanSubscriptionRequest request = new PlanSubscriptionRequest();
        request.setUserId(null);
        request.setPlanId(null);

        // Verify that both fields are null
        assertNull(request.getUserId(), "User ID should be null.");
        assertNull(request.getPlanId(), "Plan ID should be null.");
    }

    @Test
    void testBothUserIdAndPlanIdEmpty() {
        // Create a PlanSubscriptionRequest object with both userId and planId as empty
        PlanSubscriptionRequest request = new PlanSubscriptionRequest();
        request.setUserId("");
        request.setPlanId("");  // Empty string for both userId and planId

        // Verify that both fields are empty
        assertEquals("", request.getUserId(), "User ID should be empty.");
        assertEquals("", request.getPlanId(), "Plan ID should be empty.");
    }
}
