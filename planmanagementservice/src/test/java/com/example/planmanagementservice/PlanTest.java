package com.example.planmanagementservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.planmanagementservice.model.Plan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlanTest {

    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = new Plan();
    }

    @Test
    void testNoArgsConstructor() {
        assertThat(plan).isNotNull();
        assertThat(plan.getId()).isNull();
        assertThat(plan.getName()).isNull();
        assertThat(plan.getDescription()).isNull();
        assertThat(plan.getPrice()).isNull();
        assertThat(plan.getDuration()).isNull();
        assertThat(plan.getDataLimit()).isNull();
        assertThat(plan.getSmsLimit()).isNull();
        assertThat(plan.getTalkTimeMinutes()).isNull();
        assertThat(plan.getFeatures()).isNull();
        assertThat(plan.isActive()).isFalse();
        assertThat(plan.getCreatedAt()).isNull();
        assertThat(plan.getUpdatedAt()).isNull();
    }

    @Test
    void testAllFields() {
        LocalDateTime now = LocalDateTime.now();
        List<String> featureList = Arrays.asList("Unlimited Calls", "5G Data");

        plan.setId("plan123");
        plan.setName("Premium Plan");
        plan.setDescription("Best plan with 5G support");
        plan.setPrice(BigDecimal.valueOf(49.99));
        plan.setDuration(30);
        plan.setDataLimit(100);
        plan.setSmsLimit(200);
        plan.setTalkTimeMinutes("300");
        plan.setFeatures(featureList);
        plan.setActive(true);
        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);

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
    void testOnCreate() {
        plan.onCreate();

        assertThat(plan.getCreatedAt()).isNotNull();
        assertThat(plan.getUpdatedAt()).isNotNull();
        assertThat(plan.getCreatedAt()).isEqualTo(plan.getUpdatedAt());
    }

    @Test
    void testOnUpdate() {
        plan.onCreate();
        LocalDateTime createdAt = plan.getCreatedAt();

        //  Instead of Thread.sleep(), simulate a later time using `plusNanos()`
        plan.setUpdatedAt(createdAt.plusNanos(1)); // Simulates an immediate update
        plan.onUpdate();

        assertThat(plan.getUpdatedAt()).isAfter(createdAt);
    }

    @Test
    void testToString() {
        plan.setId("plan000");
        plan.setName("Test Plan");
        plan.setDescription("Sample plan for testing");
        plan.setPrice(BigDecimal.valueOf(15.99));
        plan.setDuration(5);
        plan.setDataLimit(10);
        plan.setSmsLimit(20);
        plan.setTalkTimeMinutes("50");
        plan.setFeatures(Arrays.asList("Feature1", "Feature2"));
        plan.setActive(false);
        LocalDateTime now = LocalDateTime.now();
        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);

        String toStringOutput = plan.toString();
        System.out.println("Generated toString(): " + toStringOutput);

        assertThat(toStringOutput)
                .contains("plan000", "Test Plan", "Sample plan for testing", "15.99", "5", "10", "20", "50", "Feature1", "Feature2");
    }
}
