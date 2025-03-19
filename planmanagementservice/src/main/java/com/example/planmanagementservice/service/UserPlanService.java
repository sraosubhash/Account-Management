package com.example.planmanagementservice.service;

import com.example.planmanagementservice.client.AuthServiceClient;
import com.example.planmanagementservice.dto.PlanUsageDTO;
import com.example.planmanagementservice.dto.PlanHistoryResponse;
import com.example.planmanagementservice.dto.UserPlanResponse;
import com.example.planmanagementservice.exception.ResourceNotFoundException;
import com.example.planmanagementservice.model.Plan;
import com.example.planmanagementservice.model.Role;
import com.example.planmanagementservice.model.UserPlan;
import com.example.planmanagementservice.model.PlanStatus;
import com.example.planmanagementservice.exception.PlanManagementException;
import com.example.planmanagementservice.mapper.UserPlanMapper;
import com.example.planmanagementservice.repository.PlanRepository;
import com.example.planmanagementservice.repository.UserPlanRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor

//Subhash part

public class UserPlanService {
    private final UserPlanRepository userPlanRepository;
    private final PlanRepository planRepository;
    private final UserPlanMapper userPlanMapper;
    private final UsageTrackingService usageTrackingService;
    private final AuthServiceClient authServiceClient;

    public void checkForUserExistence(String userId, String role) {
        boolean userExists = Boolean.TRUE.equals(authServiceClient.validateRole(Long.parseLong(userId), role)
                .getBody());

        if (!userExists) {
            throw new ResourceNotFoundException(role + " not found with ID: " + userId);
        }
    }
    
    private Logger log = Logger.getLogger(UserPlanService.class);
    @Transactional
    public UserPlanResponse subscribeToPlan(String userId, String planId) {
    	
        log.info("Processing plan subscription for userId: {} and planId: {}"+ userId + planId);

        checkForUserExistence(userId, Role.USER.toString());

        validateSubscriptionLimit(userId);
        Plan plan = getPlanById(planId);
        UserPlan currentPlan = getCurrentActivePlan(userId);
        UserPlan newUserPlan = createNewSubscription(userId, plan, currentPlan);

        UserPlan savedUserPlan = userPlanRepository.save(newUserPlan);
        log.info("Plan subscription created successfully for userId: {}"+ userId);

        return userPlanMapper.toResponse(savedUserPlan);
    }

    private void validateSubscriptionLimit(String userId) {
        checkForUserExistence(userId, Role.USER.toString());
        long activePlansCount = userPlanRepository.countByUserIdAndStatusIn(
                userId,
                List.of(PlanStatus.ACTIVE, PlanStatus.UPCOMING)
        );

        if (activePlansCount >= 2) {
            throw new PlanManagementException("User cannot subscribe to more than 2 plans");
        }
    }

    public Plan getPlanById(String planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanManagementException("Plan not found with id: " + planId));
    }

    public UserPlan getCurrentActivePlan(String userId) {
        checkForUserExistence(userId, Role.USER.toString());
        return userPlanRepository.findByUserIdAndStatus(userId, PlanStatus.ACTIVE)
                .orElse(null);
    }

    public UserPlan createNewSubscription(String userId, Plan plan, UserPlan currentPlan) {
        checkForUserExistence(userId, Role.USER.toString());
        UserPlan newUserPlan = new UserPlan();
        newUserPlan.setUserId(userId);
        newUserPlan.setPlan(plan);

        if (currentPlan == null) {
            newUserPlan.setStartDate(LocalDateTime.now());
            newUserPlan.setStatus(PlanStatus.ACTIVE);
        } else {
            newUserPlan.setStartDate(currentPlan.getEndDate());
            newUserPlan.setStatus(PlanStatus.UPCOMING);
        }

        newUserPlan.setEndDate(newUserPlan.getStartDate().plusDays(plan.getDuration()));
        return newUserPlan;
    }

    public PlanHistoryResponse getUserPlanHistory(String userId) {
        checkForUserExistence(userId, Role.USER.toString());
        List<UserPlan> userPlans = userPlanRepository.findByUserIdOrderByStartDateDesc(userId);
        return userPlanMapper.toHistoryResponse(userId, userPlans);
    }

    public UserPlanResponse getActiveUserPlan(String userId) {
        checkForUserExistence(userId, Role.USER.toString());
        UserPlan activePlan = userPlanRepository.findByUserIdAndStatus(userId, PlanStatus.ACTIVE)
                .orElseThrow(() -> new PlanManagementException("No active plan found for user: " + userId));
        return userPlanMapper.toResponse(activePlan);
    }

    public PlanUsageDTO getPlanUsage(String userId) {
        checkForUserExistence(userId, Role.USER.toString());
        UserPlan activePlan = getCurrentActivePlan(userId);
        if (activePlan == null) {
            throw new PlanManagementException("No active plan found for user: " + userId);
        }

        double totalDataUsed = usageTrackingService.getTotalDataUsed(userId);
        double totalSMSUsed = usageTrackingService.getTotalSMSUsed(userId);
        double totalTalkTimeMinutesUsed = usageTrackingService.getTotalTalkTimeMinutesUsed();
        long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), activePlan.getEndDate());

        return PlanUsageDTO.builder()
                .totalDataUsed(BigDecimal.valueOf(totalDataUsed))
                .totalSMSUsed(BigDecimal.valueOf(totalSMSUsed))
                .totalTalkTimeUsed(BigDecimal.valueOf(totalTalkTimeMinutesUsed))
                .dataLimit(BigDecimal.valueOf(activePlan.getPlan().getDataLimit()))
                .smsLimit(BigDecimal.valueOf(activePlan.getPlan().getSmsLimit()))
                .talkTimeMinutes(String.valueOf(activePlan.getPlan().getTalkTimeMinutes()))
                .usagePercentage(BigDecimal.valueOf((totalDataUsed / activePlan.getPlan().getDataLimit()) * 100)
                        .setScale(2, RoundingMode.HALF_UP))
                .remainingDays((int) remainingDays)
                .status(activePlan.getStatus().toString())
                .build();
    }

    @Transactional
    public void cancelSubscription(String subscriptionId) {
        UserPlan subscription = userPlanRepository.findById(subscriptionId)
                .orElseThrow(() -> new PlanManagementException("Subscription not found with id: " + subscriptionId));

        if (subscription.getStatus() != PlanStatus.UPCOMING) {
            throw new PlanManagementException("Only upcoming subscriptions can be cancelled");
        }

        userPlanRepository.delete(subscription);
        log.info("Subscription cancelled successfully: {}"+ subscriptionId);
    }
}