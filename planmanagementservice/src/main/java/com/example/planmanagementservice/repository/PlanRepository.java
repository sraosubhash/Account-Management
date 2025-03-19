package com.example.planmanagementservice.repository;

import com.example.planmanagementservice.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanRepository extends JpaRepository<Plan, String> {

	boolean existsByName(String name);
    Page<Plan> findByActiveTrue(Pageable pageable);
    
    
    
//      @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM plans WHERE name = :name", nativeQuery = true)
//      boolean existsByName(@Param("name") String name);
    
//    @Query(value = "SELECT * FROM plans WHERE active = TRUE", 
//            countQuery = "SELECT COUNT(*) FROM plans WHERE active = TRUE", 
//            nativeQuery = true)
//     Page<Plan> findByActiveTrue(Pageable pageable);

}