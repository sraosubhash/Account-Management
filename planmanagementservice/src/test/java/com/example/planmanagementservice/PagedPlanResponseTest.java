package com.example.planmanagementservice;
import org.junit.jupiter.api.Test;
import com.example.planmanagementservice.dto.PagedPlanResponse;
import com.example.planmanagementservice.dto.PlanResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PagedPlanResponseTest {

    @Test
    void testPagedPlanResponseConstructorAndGetters() {
        // Prepare sample PlanResponse list with the correct constructor arguments
        PlanResponse planResponse1 = new PlanResponse(
                "1", "Plan1", "Description1", BigDecimal.valueOf(100.0), 
                30, 50, 100, "200", Arrays.asList("Feature1", "Feature2"), 
                true, LocalDateTime.now(), LocalDateTime.now()
        );
        PlanResponse planResponse2 = new PlanResponse(
                "2", "Plan2", "Description2", BigDecimal.valueOf(200.0), 
                60, 100, 200, "400", Arrays.asList("Feature3", "Feature4"), 
                true, LocalDateTime.now(), LocalDateTime.now()
        );

        // Now create PagedPlanResponse with the above PlanResponse objects
        PagedPlanResponse pagedPlanResponse = new PagedPlanResponse(
                Arrays.asList(planResponse1, planResponse2),
                1, 10, 2, 1, true, false
        );

        // Verify the values assigned via the constructor
        assertEquals(2, pagedPlanResponse.getPlans().size(), "Plans list size should be 2");
        assertEquals(1, pagedPlanResponse.getPageNumber(), "Page number should be 1");
        assertEquals(10, pagedPlanResponse.getPageSize(), "Page size should be 10");
        assertEquals(2, pagedPlanResponse.getTotalElements(), "Total elements should be 2");
        assertEquals(1, pagedPlanResponse.getTotalPages(), "Total pages should be 1");
        assertTrue(pagedPlanResponse.isHasNext(), "Should have a next page");
        assertFalse(pagedPlanResponse.isHasPrevious(), "Should not have a previous page");
    }

    @Test
    void testSetters() {
        // Create a new PagedPlanResponse object
        PagedPlanResponse pagedPlanResponse = new PagedPlanResponse();

        // Create sample data for testing setters
        PlanResponse planResponse = new PlanResponse(
                "1", "Plan1", "Description1", BigDecimal.valueOf(100.0), 
                30, 50, 100, "200", Arrays.asList("Feature1"), 
                true, LocalDateTime.now(), LocalDateTime.now()
        );
        pagedPlanResponse.setPlans(Arrays.asList(planResponse));
        pagedPlanResponse.setPageNumber(2);
        pagedPlanResponse.setPageSize(5);
        pagedPlanResponse.setTotalElements(5);
        pagedPlanResponse.setTotalPages(1);
        pagedPlanResponse.setHasNext(true);
        pagedPlanResponse.setHasPrevious(true);

        // Test setter functionality
        assertEquals(1, pagedPlanResponse.getPlans().size(), "Plans list should contain 1 plan");
        assertEquals(2, pagedPlanResponse.getPageNumber(), "Page number should be 2");
        assertEquals(5, pagedPlanResponse.getPageSize(), "Page size should be 5");
        assertEquals(5, pagedPlanResponse.getTotalElements(), "Total elements should be 5");
        assertEquals(1, pagedPlanResponse.getTotalPages(), "Total pages should be 1");
        assertTrue(pagedPlanResponse.isHasNext(), "Should have a next page");
        assertTrue(pagedPlanResponse.isHasPrevious(), "Should have a previous page");
    }

    @Test
    void testToString() {
        // Create sample PlanResponse list with all the required fields
        PlanResponse planResponse1 = new PlanResponse(
                "1", "Plan1", "Description1", BigDecimal.valueOf(100.0), 
                30, 50, 100, "200", Arrays.asList("Feature1"), 
                true, LocalDateTime.now(), LocalDateTime.now()
        );
        PlanResponse planResponse2 = new PlanResponse(
                "2", "Plan2", "Description2", BigDecimal.valueOf(200.0), 
                60, 100, 200, "400", Arrays.asList("Feature2"), 
                true, LocalDateTime.now(), LocalDateTime.now()
        );
        
        // Create a PagedPlanResponse object
        PagedPlanResponse pagedPlanResponse = new PagedPlanResponse(
                Arrays.asList(planResponse1, planResponse2),
                1, 10, 2, 1, true, false
        );

        // Get the toString representation
        String result = pagedPlanResponse.toString();

        // Assert that the toString contains relevant information
        assertTrue(result.contains("Plan1"), "ToString should contain the name of the first plan.");
        assertTrue(result.contains("Description1"), "ToString should contain the description of the first plan.");
        assertTrue(result.contains("true"), "ToString should indicate whether there's a next page.");
        assertTrue(result.contains("false"), "ToString should indicate whether there's a previous page.");
    }
    @Test
    void testEmptyPagedPlanResponse() {
        // Create an empty PagedPlanResponse object
        PagedPlanResponse pagedPlanResponse = new PagedPlanResponse();
        
        // Initialize the plans field to avoid NullPointerException
        pagedPlanResponse.setPlans(new ArrayList<>());

        // Verify the default values of the empty object
        assertEquals(0, pagedPlanResponse.getPlans().size(), "Plans list should be empty by default");
        assertEquals(0, pagedPlanResponse.getPageNumber(), "Page number should be 0 by default");
        assertEquals(0, pagedPlanResponse.getPageSize(), "Page size should be 0 by default");
        assertEquals(0, pagedPlanResponse.getTotalElements(), "Total elements should be 0 by default");
        assertEquals(0, pagedPlanResponse.getTotalPages(), "Total pages should be 0 by default");
        assertFalse(pagedPlanResponse.isHasNext(), "Should not have a next page by default");
        assertFalse(pagedPlanResponse.isHasPrevious(), "Should not have a previous page by default");
    }


}
