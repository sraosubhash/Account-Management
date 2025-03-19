package com.example.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.planmanagementservice.dto.PlanResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.exception.PlanManagementException;
import com.example.planmanagementservice.mapper.PlanMapper;
import com.example.planmanagementservice.mapper.UserPlanMapper;
import com.example.planmanagementservice.model.Plan;
import com.example.planmanagementservice.model.PlanStatus;
import com.example.planmanagementservice.model.UserPlan;
import com.example.planmanagementservice.repository.PlanRepository;
import com.example.planmanagementservice.repository.UserPlanRepository;
import com.example.planmanagementservice.service.AdminPlanService;

@ExtendWith(MockitoExtension.class)
 class AdminPlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private UserPlanRepository userPlanRepository;

    @Mock
    private PlanMapper planMapper;

    @Mock
    private UserPlanMapper userPlanMapper;

    @InjectMocks
    private AdminPlanService adminPlanService;

    private Plan plan;
    private UserPlan userPlan;
    private PlanResponse planResponse;
    private UserPlanResponse userPlanResponse;

    @BeforeEach
    void setUp() {
        plan = new Plan();
        plan.setId("plan123");
        plan.setActive(false);

        userPlan = new UserPlan();
        userPlan.setId("userPlan123");
        userPlan.setUserId("1L");
        userPlan.setStartDate(LocalDateTime.now().minusDays(10));
        userPlan.setEndDate(LocalDateTime.now().plusDays(10));
        userPlan.setStatus(PlanStatus.ACTIVE);

        planResponse = new PlanResponse();
        userPlanResponse = new UserPlanResponse();
    }

    @Test
    void testGetAllPlans() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Plan> planPage = new PageImpl<>(List.of(plan));
        when(planRepository.findAll(pageable)).thenReturn(planPage);
        when(planMapper.toResponse(plan)).thenReturn(planResponse);

        // Act
        Page<PlanResponse> result = adminPlanService.getAllPlans(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(planRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllSubscriptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserPlan> userPlanPage = new PageImpl<>(List.of(userPlan));
        when(userPlanRepository.findAll(pageable)).thenReturn(userPlanPage);
        when(userPlanMapper.toResponse(userPlan)).thenReturn(userPlanResponse);

        // Act
        Page<UserPlanResponse> result = adminPlanService.getAllSubscriptions(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userPlanRepository, times(1)).findAll(pageable);
    }

    @Test
    void testActivatePlan_Success() {
        // Arrange
        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);
        when(planMapper.toResponse(plan)).thenReturn(planResponse);

        // Act
        PlanResponse result = adminPlanService.activatePlan("plan123");

        // Assert
        assertNotNull(result);
        assertTrue(plan.isActive());
        verify(planRepository, times(1)).save(plan);
    }

    @Test
    void testActivatePlan_PlanNotFound() {
        // Arrange
        when(planRepository.findById("invalidPlan")).thenReturn(Optional.empty());

        // Act & Assert
        PlanManagementException exception = assertThrows(PlanManagementException.class, 
                () -> adminPlanService.activatePlan("invalidPlan"));

        assertEquals("Plan not found with id: invalidPlan", exception.getMessage());
    }

    @Test
    void testDeactivatePlan_Success() {
        // Arrange
        plan.setActive(true);
        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);
        when(planMapper.toResponse(plan)).thenReturn(planResponse);

        // Act
        PlanResponse result = adminPlanService.deactivatePlan("plan123");

        // Assert
        assertNotNull(result);
        assertFalse(plan.isActive());
        verify(planRepository, times(1)).save(plan);
    }

    @Test
    void testUpdatePlanStatuses() {
        // Arrange
        userPlan.setStatus(PlanStatus.UPCOMING);
        List<UserPlan> plans = List.of(userPlan);

        when(userPlanRepository.findByStatusIn(List.of(PlanStatus.ACTIVE, PlanStatus.UPCOMING)))
                .thenReturn(plans);

        // Act
        adminPlanService.updatePlanStatuses();

        // Assert
        assertEquals(PlanStatus.ACTIVE, userPlan.getStatus());
        verify(userPlanRepository, times(1)).saveAll(plans);
    }
}
