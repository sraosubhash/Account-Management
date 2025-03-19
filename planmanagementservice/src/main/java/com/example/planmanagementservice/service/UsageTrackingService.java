package com.example.planmanagementservice.service;
 
import com.example.planmanagementservice.model.PlanStatus;
import com.example.planmanagementservice.model.UserPlan;
import com.example.planmanagementservice.repository.UserPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.Random;
 
@Service
@RequiredArgsConstructor

//Subhash part

public class UsageTrackingService {
    private final UserPlanRepository userPlanRepository;
    private final Random random = new Random();
 
    public double getTotalDataUsed(String userId) {
        UserPlan activePlan = getActivePlan(userId);
        double maxData = activePlan.getPlan().getDataLimit();
        return calculateUsage(maxData, 0.1, 0.8);
    }
 
    public double getTotalSMSUsed(String userId) {
        UserPlan activePlan = getActivePlan(userId);
        double maxSMS = activePlan.getPlan().getSmsLimit();
        return calculateUsage(maxSMS, 0.1, 0.8);
    }
 
   
 
    private UserPlan getActivePlan(String userId) {
        return userPlanRepository.findByUserIdAndStatus(userId, PlanStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active plan found for user: " + userId));
    }
 
    private double calculateUsage(double maxLimit, double minFactor, double maxFactor) {
        double factor = minFactor + (maxFactor - minFactor) * random.nextDouble(); 
        return Math.round((maxLimit * factor) * 10.0) / 10.0; 
    }
 
 
    public double getTotalTalkTimeMinutesUsed() {
        return 47.5;
    }
}