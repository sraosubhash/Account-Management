package com.example.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.planmanagementservice.model.Plan;
import com.example.planmanagementservice.model.PlanStatus;
import com.example.planmanagementservice.model.UserPlan;
import com.example.planmanagementservice.repository.UserPlanRepository;
import com.example.planmanagementservice.service.UsageTrackingService;

@ExtendWith(MockitoExtension.class)
class UsageTrackingServiceTest {

    @Mock
    private UserPlanRepository userPlanRepository;

    @Mock
    private Random random;  //Now properly mocked

    @InjectMocks
    private UsageTrackingService usageTrackingService;

    private UserPlan userPlan;
    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = new Plan();
        plan.setDataLimit(100);
        plan.setSmsLimit(1000);

        userPlan = new UserPlan();
        userPlan.setUserId("user123");
        userPlan.setStatus(PlanStatus.ACTIVE);
        userPlan.setPlan(plan);

        when(userPlanRepository.findByUserIdAndStatus(anyString(), any(PlanStatus.class)))
                .thenReturn(Optional.of(userPlan));
    }

    @Test
    void testGetTotalDataUsed_Success() {
        when(random.nextDouble()).thenReturn(0.5);  
        double dataUsed = usageTrackingService.getTotalDataUsed("user123");

        assertEquals(50.0, dataUsed, 0.1);  
    }

    @Test
    void testGetTotalDataUsed_NoActivePlan() {
        when(userPlanRepository.findByUserIdAndStatus(anyString(), any(PlanStatus.class)))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usageTrackingService.getTotalDataUsed("user123"));

        assertEquals("No active plan found for user: user123", exception.getMessage());
    }

    @Test
    void testGetTotalSMSUsed_Success() {
        when(random.nextDouble()).thenReturn(0.3);

        double smsUsed = usageTrackingService.getTotalSMSUsed("user123");

        assertEquals(30.0, smsUsed, 0.1);
    }

    @Test
    void testGetTotalSMSUsed_NoActivePlan() {
        // Arrange - Ensure repository returns empty result
        when(userPlanRepository.findByUserIdAndStatus(anyString(), any(PlanStatus.class)))
                .thenReturn(Optional.empty());

        // Act & Assert - Expect RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> usageTrackingService.getTotalSMSUsed("user123"));

        assertEquals("No active plan found for user: user123", exception.getMessage());
    }


}
