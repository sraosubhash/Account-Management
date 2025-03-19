package com.example.planmanagementservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.model.PlanStatus;
import com.example.planmanagementservice.model.UserPlan;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;

class UserPlanTest {
    private UserPlan userPlan;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        userPlan = new UserPlan();
        userPlan.setId("1234-uuid");
        userPlan.setUserId("user-5678");
        userPlan.setStartDate(now);
        userPlan.setEndDate(now.plusDays(30));
        userPlan.setStatus(PlanStatus.ACTIVE);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("1234-uuid", userPlan.getId());
        assertEquals("user-5678", userPlan.getUserId());
        assertEquals(now, userPlan.getStartDate());
        assertEquals(now.plusDays(30), userPlan.getEndDate());
        assertEquals(PlanStatus.ACTIVE, userPlan.getStatus());
    }

    @Test
    void testPrePersist() throws Exception {
        Method onCreateMethod = UserPlan.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(userPlan);

        assertNotNull(userPlan.getCreatedAt(), "createdAt should not be null after @PrePersist");
        assertNotNull(userPlan.getUpdatedAt(), "updatedAt should not be null after @PrePersist");
       
    }

    @Test
    void testPreUpdate() throws Exception {
        Method onUpdateMethod = UserPlan.class.getDeclaredMethod("onUpdate");
        onUpdateMethod.setAccessible(true);
        onUpdateMethod.invoke(userPlan);

        assertNotNull(userPlan.getUpdatedAt(), "updatedAt should not be null after @PreUpdate");
    }

    @Test
    void testToString() {
        String result = userPlan.toString();
        
        assertTrue(result.contains("1234-uuid"));
        assertTrue(result.contains("user-5678"));
        assertTrue(result.contains("ACTIVE"));
    }
}
