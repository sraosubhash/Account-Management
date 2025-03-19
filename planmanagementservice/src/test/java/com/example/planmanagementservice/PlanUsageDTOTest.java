package com.example.planmanagementservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.dto.PlanUsageDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PlanUsageDTOTest {

    private PlanUsageDTO planUsageDTO;

    @BeforeEach
    void setUp() {
        planUsageDTO = new PlanUsageDTO();
    }

    @Test
    void testNoArgsConstructor() {
        assertThat(planUsageDTO).isNotNull();
        assertThat(planUsageDTO.getTotalDataUsed()).isNull();
        assertThat(planUsageDTO.getTotalSMSUsed()).isNull();
        assertThat(planUsageDTO.getTotalTalkTimeUsed()).isNull();
        assertThat(planUsageDTO.getDataLimit()).isNull();
        assertThat(planUsageDTO.getSmsLimit()).isNull();
        assertThat(planUsageDTO.getTalkTimeMinutes()).isNull();
        assertThat(planUsageDTO.getUsagePercentage()).isNull();
        assertThat(planUsageDTO.getRemainingDays()).isZero();
        assertThat(planUsageDTO.getStatus()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        PlanUsageDTO planUsage = new PlanUsageDTO(
                BigDecimal.valueOf(50.5),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(1000),
                "300",
                BigDecimal.valueOf(75.0),
                10,
                "Active"
        );

        assertThat(planUsage.getTotalDataUsed()).isEqualByComparingTo("50.5");
        assertThat(planUsage.getTotalSMSUsed()).isEqualByComparingTo("100");
        assertThat(planUsage.getTotalTalkTimeUsed()).isEqualByComparingTo("200");
        assertThat(planUsage.getDataLimit()).isEqualByComparingTo("500");
        assertThat(planUsage.getSmsLimit()).isEqualByComparingTo("1000");
        assertThat(planUsage.getTalkTimeMinutes()).isEqualTo("300");
        assertThat(planUsage.getUsagePercentage()).isEqualByComparingTo("75.0");
        assertThat(planUsage.getRemainingDays()).isEqualTo(10);
        assertThat(planUsage.getStatus()).isEqualTo("Active");
    }

    @Test
    void testBuilder() {
        PlanUsageDTO planUsage = PlanUsageDTO.builder()
                .totalDataUsed(BigDecimal.valueOf(60))
                .totalSMSUsed(BigDecimal.valueOf(200))
                .totalTalkTimeUsed(BigDecimal.valueOf(400))
                .dataLimit(BigDecimal.valueOf(800))
                .smsLimit(BigDecimal.valueOf(1600))
                .talkTimeMinutes("500")
                .usagePercentage(BigDecimal.valueOf(80.0))
                .remainingDays(5)
                .status("Expired")
                .build();

        assertThat(planUsage.getTotalDataUsed()).isEqualByComparingTo("60");
        assertThat(planUsage.getTotalSMSUsed()).isEqualByComparingTo("200");
        assertThat(planUsage.getTotalTalkTimeUsed()).isEqualByComparingTo("400");
        assertThat(planUsage.getDataLimit()).isEqualByComparingTo("800");
        assertThat(planUsage.getSmsLimit()).isEqualByComparingTo("1600");
        assertThat(planUsage.getTalkTimeMinutes()).isEqualTo("500");
        assertThat(planUsage.getUsagePercentage()).isEqualByComparingTo("80.0");
        assertThat(planUsage.getRemainingDays()).isEqualTo(5);
        assertThat(planUsage.getStatus()).isEqualTo("Expired");
    }

    @Test
    void testSettersAndGetters() {
        planUsageDTO.setTotalDataUsed(BigDecimal.valueOf(75));
        planUsageDTO.setTotalSMSUsed(BigDecimal.valueOf(150));
        planUsageDTO.setTotalTalkTimeUsed(BigDecimal.valueOf(250));
        planUsageDTO.setDataLimit(BigDecimal.valueOf(600));
        planUsageDTO.setSmsLimit(BigDecimal.valueOf(1200));
        planUsageDTO.setTalkTimeMinutes("350");
        planUsageDTO.setUsagePercentage(BigDecimal.valueOf(65.0));
        planUsageDTO.setRemainingDays(7);
        planUsageDTO.setStatus("Inactive");

        assertThat(planUsageDTO.getTotalDataUsed()).isEqualByComparingTo("75");
        assertThat(planUsageDTO.getTotalSMSUsed()).isEqualByComparingTo("150");
        assertThat(planUsageDTO.getTotalTalkTimeUsed()).isEqualByComparingTo("250");
        assertThat(planUsageDTO.getDataLimit()).isEqualByComparingTo("600");
        assertThat(planUsageDTO.getSmsLimit()).isEqualByComparingTo("1200");
        assertThat(planUsageDTO.getTalkTimeMinutes()).isEqualTo("350");
        assertThat(planUsageDTO.getUsagePercentage()).isEqualByComparingTo("65.0");
        assertThat(planUsageDTO.getRemainingDays()).isEqualTo(7);
        assertThat(planUsageDTO.getStatus()).isEqualTo("Inactive");
    }

    @Test
    void testToString() {
        planUsageDTO.setTotalDataUsed(BigDecimal.valueOf(25));
        planUsageDTO.setTotalSMSUsed(BigDecimal.valueOf(50));
        planUsageDTO.setTotalTalkTimeUsed(BigDecimal.valueOf(75));
        planUsageDTO.setDataLimit(BigDecimal.valueOf(250));
        planUsageDTO.setSmsLimit(BigDecimal.valueOf(500));
        planUsageDTO.setTalkTimeMinutes("100");
        planUsageDTO.setUsagePercentage(BigDecimal.valueOf(50.0));
        planUsageDTO.setRemainingDays(3);
        planUsageDTO.setStatus("Pending");

        String toStringOutput = planUsageDTO.toString();
        System.out.println("Generated toString(): " + toStringOutput);

        assertThat(toStringOutput).contains("25", "50", "75", "250", "500", "100", "50.0", "3", "Pending");
    }
}
