package com.example.planmanagementservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanRequest {
    @NotBlank(message = "Plan name is required")
    @Size(min = 3, max = 50, message = "Plan name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Plan description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    @Max(value = 365, message = "Duration cannot exceed 365 days")
    private Integer duration;

    @NotNull(message = "Data limit is required")
    @Min(value = 1, message = "Data limit must be at least 1 GB")
    private Integer dataLimit;

    @NotNull(message = "SMS limit is required")
    private Integer smsLimit;

    @NotNull(message = "Talk Time Minutes is required")
    private String talkTimeMinutes;

    @NotEmpty(message = "At least one feature is required")
    private List<String> features;



    private boolean active = true;
}