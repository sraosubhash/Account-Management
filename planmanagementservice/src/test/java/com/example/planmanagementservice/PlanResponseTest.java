package com.example.planmanagementservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.dto.PlanResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlanResponseTest {

    private PlanResponse planResponse;

    @BeforeEach
    void setUp() {
        planResponse = new PlanResponse();
    }

    @Test
    void testNoArgsConstructor() {
        assertThat(planResponse).isNotNull();
        assertThat(planResponse.getId()).isNull();
        assertThat(planResponse.getName()).isNull();
        assertThat(planResponse.getDescription()).isNull();
        assertThat(planResponse.getPrice()).isNull();
        assertThat(planResponse.getDuration()).isNull();
        assertThat(planResponse.getDataLimit()).isNull();
        assertThat(planResponse.getSmsLimit()).isNull();
        assertThat(planResponse.getTalkTimeMinutes()).isNull();
        assertThat(planResponse.getFeatures()).isNull();
        assertThat(planResponse.isActive()).isFalse();
        assertThat(planResponse.getCreatedAt()).isNull();
        assertThat(planResponse.getUpdatedAt()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<String> featureList = Arrays.asList("Unlimited Calls", "5G Data");

        PlanResponse plan = new PlanResponse(
                "plan123", "Premium Plan", "Best plan with 5G support",
                BigDecimal.valueOf(49.99), 30, 100, 200, "300",
                featureList, true, now, now
        );

        assertThat(plan.getId()).isEqualTo("plan123");
        assertThat(plan.getName()).isEqualTo("Premium Plan");
        assertThat(plan.getDescription()).isEqualTo("Best plan with 5G support");
        assertThat(plan.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(49.99));
        assertThat(plan.getDuration()).isEqualTo(30);
        assertThat(plan.getDataLimit()).isEqualTo(100);
        assertThat(plan.getSmsLimit()).isEqualTo(200);
        assertThat(plan.getTalkTimeMinutes()).isEqualTo("300");
        assertThat(plan.getFeatures()).containsExactly("Unlimited Calls", "5G Data");
        assertThat(plan.isActive()).isTrue();
        assertThat(plan.getCreatedAt()).isEqualTo(now);
        assertThat(plan.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        List<String> featureList = Arrays.asList("4G Data", "International Roaming");

        PlanResponse plan = PlanResponse.builder()
                .id("plan456")
                .name("Standard Plan")
                .description("Good plan for travelers")
                .price(BigDecimal.valueOf(29.99))
                .duration(15)
                .dataLimit(50)
                .smsLimit(100)
                .talkTimeMinutes("150")
                .features(featureList)
                .active(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(plan.getId()).isEqualTo("plan456");
        assertThat(plan.getName()).isEqualTo("Standard Plan");
        assertThat(plan.getDescription()).isEqualTo("Good plan for travelers");
        assertThat(plan.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(29.99));
        assertThat(plan.getDuration()).isEqualTo(15);
        assertThat(plan.getDataLimit()).isEqualTo(50);
        assertThat(plan.getSmsLimit()).isEqualTo(100);
        assertThat(plan.getTalkTimeMinutes()).isEqualTo("150");
        assertThat(plan.getFeatures()).containsExactly("4G Data", "International Roaming");
        assertThat(plan.isActive()).isFalse();
        assertThat(plan.getCreatedAt()).isEqualTo(now);
        assertThat(plan.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        List<String> featureList = Arrays.asList("VoLTE", "Free Streaming");

        planResponse.setId("plan789");
        planResponse.setName("Basic Plan");
        planResponse.setDescription("Affordable plan with essential features");
        planResponse.setPrice(BigDecimal.valueOf(19.99));
        planResponse.setDuration(7);
        planResponse.setDataLimit(25);
        planResponse.setSmsLimit(50);
        planResponse.setTalkTimeMinutes("100");
        planResponse.setFeatures(featureList);
        planResponse.setActive(true);
        planResponse.setCreatedAt(now);
        planResponse.setUpdatedAt(now);

        assertThat(planResponse.getId()).isEqualTo("plan789");
        assertThat(planResponse.getName()).isEqualTo("Basic Plan");
        assertThat(planResponse.getDescription()).isEqualTo("Affordable plan with essential features");
        assertThat(planResponse.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(19.99));
        assertThat(planResponse.getDuration()).isEqualTo(7);
        assertThat(planResponse.getDataLimit()).isEqualTo(25);
        assertThat(planResponse.getSmsLimit()).isEqualTo(50);
        assertThat(planResponse.getTalkTimeMinutes()).isEqualTo("100");
        assertThat(planResponse.getFeatures()).containsExactly("VoLTE", "Free Streaming");
        assertThat(planResponse.isActive()).isTrue();
        assertThat(planResponse.getCreatedAt()).isEqualTo(now);
        assertThat(planResponse.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testToString() {
        planResponse.setId("plan000");
        planResponse.setName("Test Plan");
        planResponse.setDescription("Sample plan for testing");
        planResponse.setPrice(BigDecimal.valueOf(15.99));
        planResponse.setDuration(5);
        planResponse.setDataLimit(10);
        planResponse.setSmsLimit(20);
        planResponse.setTalkTimeMinutes("50");
        planResponse.setFeatures(Arrays.asList("Feature1", "Feature2"));
        planResponse.setActive(false);
        LocalDateTime now = LocalDateTime.now();
        planResponse.setCreatedAt(now);
        planResponse.setUpdatedAt(now);

        String toStringOutput = planResponse.toString();
        System.out.println("Generated toString(): " + toStringOutput);

        assertThat(toStringOutput).contains("plan000", "Test Plan", "Sample plan for testing", "15.99", "5", "10", "20", "50", "Feature1", "Feature2");
    }
}
