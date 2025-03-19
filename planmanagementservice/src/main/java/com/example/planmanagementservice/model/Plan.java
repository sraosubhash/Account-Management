package com.example.planmanagementservice.model;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration; // in days
    private Integer dataLimit; // in GB
    private Integer smsLimit;// in Number
    private String talkTimeMinutes; //Used String so we can use unlimited too.


    @ElementCollection
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features;
    
    private boolean active;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
	public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
	public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}