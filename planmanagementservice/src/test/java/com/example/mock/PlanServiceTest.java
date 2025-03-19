package com.example.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
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

import com.example.planmanagementservice.dto.CreatePlanRequest;
import com.example.planmanagementservice.dto.PagedPlanResponse;
import com.example.planmanagementservice.dto.PlanResponse;
import com.example.planmanagementservice.exception.PlanManagementException;
import com.example.planmanagementservice.mapper.PlanMapper;
import com.example.planmanagementservice.model.Plan;
import com.example.planmanagementservice.repository.PlanRepository;
import com.example.planmanagementservice.service.PlanService;

@ExtendWith(MockitoExtension.class)
 class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanMapper planMapper;

    @Mock
    private Logger log;

    @InjectMocks
    private PlanService planService;

    private Plan plan;
    private PlanResponse planResponse;
    private CreatePlanRequest createPlanRequest;


    @BeforeEach
    void setUp() {
        plan = new Plan();
        plan.setId("plan123");
        plan.setName("Basic Plan");
        plan.setActive(true);

        planResponse = new PlanResponse();
        planResponse.setId("plan123");
        planResponse.setName("Basic Plan");

        createPlanRequest = new CreatePlanRequest();
        createPlanRequest.setName("Basic Plan");

    }

    /**
     * Test for creating a new plan successfully
     */
    @Test
    void testCreatePlan_Success() {
        // Arrange
        when(planRepository.existsByName(createPlanRequest.getName())).thenReturn(false);
        when(planMapper.toEntity(createPlanRequest)).thenReturn(plan);
        when(planRepository.save(any(Plan.class))).thenReturn(plan);
        when(planMapper.toResponse(plan)).thenReturn(planResponse);

        // Act
        PlanResponse result = planService.createPlan(createPlanRequest);

        // Assert
        assertNotNull(result);
        assertEquals(planResponse.getId(), result.getId());
        verify(planRepository, times(1)).save(plan);
    }

    /**
     * Test for create plan failure due to duplicate name
     */
    @Test
    void testCreatePlan_Failure_DuplicateName() {
        // Arrange
        when(planRepository.existsByName(createPlanRequest.getName())).thenReturn(true);

        // Act & Assert
        PlanManagementException exception = assertThrows(PlanManagementException.class, 
                () -> planService.createPlan(createPlanRequest));

        assertEquals("Plan with name Basic Plan already exists", exception.getMessage());
        verify(planRepository, never()).save(any(Plan.class));
    }

    
    /**
     * Test for getting a plan by ID successfully
     */
    @Test
    void testGetPlanById_Success() {
        // Arrange
        when(planRepository.findById("plan123")).thenReturn(Optional.of(plan));
        when(planMapper.toResponse(plan)).thenReturn(planResponse);

        // Act
        PlanResponse result = planService.getPlanById("plan123");

        // Assert
        assertNotNull(result);
        assertEquals(planResponse.getId(), result.getId());
    }

    /**
     * Test for getting a non-existing plan by ID
     */
    @Test
    void testGetPlanById_NotFound() {
        // Arrange
        when(planRepository.findById("nonExistentId")).thenReturn(Optional.empty());

        // Act & Assert
        PlanManagementException exception = assertThrows(PlanManagementException.class, 
                () -> planService.getPlanById("nonExistentId"));

        assertEquals("Plan not found with id: nonExistentId", exception.getMessage());
    }

    /**
     * Test for getting all active plans
     */
    @Test
    void testGetAllActivePlans() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Plan> planPage = new PageImpl<>(List.of(plan));
        when(planRepository.findByActiveTrue(pageable)).thenReturn(planPage);

        PagedPlanResponse pagedResponse = new PagedPlanResponse();
        when(planMapper.toPagedResponse(planPage)).thenReturn(pagedResponse);

        // Act
        PagedPlanResponse result = planService.getAllActivePlans(pageable);

        // Assert
        assertNotNull(result);
        verify(planRepository, times(1)).findByActiveTrue(pageable);
    }


    /**
     * Test for validating a plan's existence
     */
    @Test
    void testValidatePlan() {
        // Arrange
        when(planRepository.existsById("plan123")).thenReturn(true);

        // Act
        boolean isValid = planService.validatePlan("plan123");

        // Assert
        assertTrue(isValid);
    }
}
