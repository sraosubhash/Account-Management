package com.example.mock;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.planmanagementservice.client.AuthServiceClient;
import com.example.planmanagementservice.dto.PlanHistoryResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.exception.PlanManagementException;
import com.example.planmanagementservice.exception.ResourceNotFoundException;
import com.example.planmanagementservice.mapper.UserPlanMapper;
import com.example.planmanagementservice.model.Plan;
import com.example.planmanagementservice.model.PlanStatus;
import com.example.planmanagementservice.model.UserPlan;
import com.example.planmanagementservice.repository.PlanRepository;
import com.example.planmanagementservice.repository.UserPlanRepository;
import com.example.planmanagementservice.service.UsageTrackingService;
import com.example.planmanagementservice.service.UserPlanService;

@ExtendWith(MockitoExtension.class)
class UserPlanServiceTest {

    @Mock
    private UserPlanRepository userPlanRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private UserPlanMapper userPlanMapper;

    @Mock
    private UsageTrackingService usageTrackingService;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private Logger log;

    @InjectMocks
    private UserPlanService userPlanService;

    private Plan plan;
    private UserPlan activeUserPlan;
    private UserPlan upcomingUserPlan;

    @BeforeEach
    void setUp() {
        plan = new Plan();
        plan.setId("plan123");
        plan.setDataLimit(100);
        plan.setSmsLimit(1000);
        plan.setDuration(30);

        activeUserPlan = new UserPlan();
        activeUserPlan.setUserId("user123");
        activeUserPlan.setPlan(plan);
        activeUserPlan.setStatus(PlanStatus.ACTIVE);
        activeUserPlan.setStartDate(LocalDateTime.now().minusDays(5));
        activeUserPlan.setEndDate(LocalDateTime.now().plusDays(25));

        upcomingUserPlan = new UserPlan();
        upcomingUserPlan.setUserId("user123");
        upcomingUserPlan.setPlan(plan);
        upcomingUserPlan.setStatus(PlanStatus.UPCOMING);
        upcomingUserPlan.setStartDate(LocalDateTime.now().plusDays(25));
        upcomingUserPlan.setEndDate(LocalDateTime.now().plusDays(55));

        when(authServiceClient.validateRole(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));
    }

    @Test
    void testSubscribeToPlan_Success() {
        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(userPlanRepository.save(any(UserPlan.class))).thenReturn(activeUserPlan);
        when(userPlanMapper.toResponse(any(UserPlan.class))).thenReturn(new UserPlanResponse());

        UserPlanResponse response = userPlanService.subscribeToPlan("user123", "plan123");

        assertNotNull(response);
        verify(userPlanRepository, times(1)).save(any(UserPlan.class));
    }

    @Test
    void testSubscribeToPlan_PlanNotFound() {
        when(planRepository.findById("invalidPlan")).thenReturn(Optional.empty());

        assertThrows(PlanManagementException.class,
                () -> userPlanService.subscribeToPlan("user123", "invalidPlan"));
    }

    @Test
    void testGetUserPlanHistory_Success() {
        when(userPlanRepository.findByUserIdOrderByStartDateDesc("user123"))
                .thenReturn(List.of(activeUserPlan, upcomingUserPlan));
        when(userPlanMapper.toHistoryResponse(anyString(), anyList()))
                .thenReturn(new PlanHistoryResponse());

        PlanHistoryResponse historyResponse = userPlanService.getUserPlanHistory("user123");

        assertNotNull(historyResponse);
    }

    @Test
    void testGetActiveUserPlan_Success() {
        when(userPlanRepository.findByUserIdAndStatus("user123", PlanStatus.ACTIVE))
                .thenReturn(Optional.of(activeUserPlan));
        when(userPlanMapper.toResponse(any(UserPlan.class))).thenReturn(new UserPlanResponse());

        UserPlanResponse response = userPlanService.getActiveUserPlan("user123");

        assertNotNull(response);
    }

    @Test
    void testGetActiveUserPlan_NoActivePlan() {
        when(userPlanRepository.findByUserIdAndStatus("user123", PlanStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(PlanManagementException.class,
                () -> userPlanService.getActiveUserPlan("user123"));
    }


    @Test
    void testGetPlanUsage_NoActivePlan() {
        when(userPlanRepository.findByUserIdAndStatus("user123", PlanStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(PlanManagementException.class, () -> userPlanService.getPlanUsage("user123"));
    }

    @Test
    void testCancelSubscription_Success() {
        when(userPlanRepository.findById("subscription123")).thenReturn(Optional.of(upcomingUserPlan));

        userPlanService.cancelSubscription("subscription123");

        verify(userPlanRepository, times(1)).delete(upcomingUserPlan);
    }

    @Test
    void testCancelSubscription_NotUpcoming() {
        activeUserPlan.setStatus(PlanStatus.ACTIVE);
        
        assertThrows(PlanManagementException.class,
                () -> userPlanService.cancelSubscription("subscription123"));
    }

    @Test
    void testCheckForUserExistence_UserNotFound() {
        when(authServiceClient.validateRole(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(false));

        assertThrows(ResourceNotFoundException.class,
                () -> userPlanService.checkForUserExistence("user123", "USER"));
    }
}
