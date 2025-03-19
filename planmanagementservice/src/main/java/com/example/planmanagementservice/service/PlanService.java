package com.example.planmanagementservice.service;

import com.example.planmanagementservice.dto.*;
import org.apache.log4j.Logger;
import com.example.planmanagementservice.model.Plan;
import com.example.planmanagementservice.exception.PlanManagementException;
import com.example.planmanagementservice.mapper.PlanMapper;
import com.example.planmanagementservice.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PlanMapper planMapper;
    private Logger log = Logger.getLogger(PlanService.class);
    String pp="Plan not found with id: ";
    @Transactional
    public PlanResponse createPlan(CreatePlanRequest request) {
        log.info("Creating new plan with name: {}"+ request.getName());

        if (planRepository.existsByName(request.getName())) {
            throw new PlanManagementException("Plan with name " + request.getName() + " already exists");
        }

        Plan plan = planMapper.toEntity(request);
        Plan savedPlan = planRepository.save(plan);
        log.info("Plan created successfully with id: {}"+ savedPlan.getId());

        return planMapper.toResponse(savedPlan);
    }

 


    public PlanResponse getPlanById(String id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new PlanManagementException(pp+ id));
        return planMapper.toResponse(plan);
    }

    public PagedPlanResponse getAllActivePlans(Pageable pageable) {
        Page<Plan> planPage = planRepository.findByActiveTrue(pageable);
        return planMapper.toPagedResponse(planPage);
    }



    public boolean validatePlan(String id) {
        return planRepository.existsById(id);
    }
}