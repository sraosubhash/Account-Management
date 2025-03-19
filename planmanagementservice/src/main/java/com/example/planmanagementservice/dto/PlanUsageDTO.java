package com.example.planmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//Subhash part

public class PlanUsageDTO {
    private BigDecimal totalDataUsed;
    private BigDecimal totalSMSUsed;
    private BigDecimal totalTalkTimeUsed;
    private BigDecimal dataLimit;
    private BigDecimal smsLimit;
    private String talkTimeMinutes;
    private BigDecimal usagePercentage;
    private int remainingDays;
    private String status;
}
