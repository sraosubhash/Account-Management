package com.example.planmanagementservice.service;

import com.example.planmanagementservice.dto.PlanResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.model.Plan;
import com.example.planmanagementservice.model.UserPlan;
import com.example.planmanagementservice.model.PlanStatus;
import com.example.planmanagementservice.exception.PlanManagementException;
import com.example.planmanagementservice.mapper.PlanMapper;
import com.example.planmanagementservice.mapper.UserPlanMapper;
import com.example.planmanagementservice.repository.PlanRepository;
import com.example.planmanagementservice.repository.UserPlanRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPlanService {
    private final PlanRepository planRepository;
    private final UserPlanRepository userPlanRepository;
    private final PlanMapper planMapper;
    private final UserPlanMapper userPlanMapper;
    
    private Logger log = Logger.getLogger(AdminPlanService.class);

    public Page<PlanResponse> getAllPlans(Pageable pageable) {
        return planRepository.findAll(pageable)
            .map(planMapper::toResponse);
    }

    public Page<UserPlanResponse> getAllSubscriptions(Pageable pageable) {
        return userPlanRepository.findAll(pageable)
            .map(userPlanMapper::toResponse);
    }

    @Transactional
    public PlanResponse activatePlan(String planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new PlanManagementException("Plan not found with id: " + planId));
        
        plan.setActive(true);
        Plan savedPlan = planRepository.save(plan);
        return planMapper.toResponse(savedPlan);
    }

    @Transactional
    public PlanResponse deactivatePlan(String planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new PlanManagementException("Plan not found with id: " + planId));
        
        plan.setActive(false);
        Plan savedPlan = planRepository.save(plan);
        return planMapper.toResponse(savedPlan);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    public void updatePlanStatuses() {
        log.info("Starting plan status update");
        LocalDateTime now = LocalDateTime.now();

        List<UserPlan> plans = userPlanRepository.findByStatusIn(
            List.of(PlanStatus.ACTIVE, PlanStatus.UPCOMING)
        );

        for (UserPlan plan : plans) {
            if (plan.getEndDate().isBefore(now)) {
                plan.setStatus(PlanStatus.EXPIRED);
                log.info("Plan expired for userId: " + plan.getUserId());
            } else if (plan.getStatus() == PlanStatus.UPCOMING &&
                       plan.getStartDate().isBefore(now)) {
                plan.setStatus(PlanStatus.ACTIVE);
                log.info("Plan activated for userId: " + plan.getUserId());
            }
        }

        userPlanRepository.saveAll(plans);
        log.info("Completed plan status update");
    }

}