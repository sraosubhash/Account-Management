package com.example.planmanagementservice.repository;

import com.example.planmanagementservice.model.UserPlan;
import com.example.planmanagementservice.model.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//Subhash part

public interface UserPlanRepository extends JpaRepository<UserPlan, String> {
	
	//Used for plan history
	List<UserPlan> findByUserIdOrderByStartDateDesc(String userId);
	//Used to validate subscription limit
    long countByUserIdAndStatusIn(String userId, List<PlanStatus> statuses);
    //Used for finding active status field
    Optional<UserPlan> findByUserIdAndStatus(String userId, PlanStatus status);
    //To update status active -> expired (AdminPlanService)
    List<UserPlan> findByStatusIn(List<PlanStatus> statuses);
	
	
	
	
		
//	@Query(value = "SELECT * FROM user_plans WHERE user_id = :userId "
//			+ "ORDER BY start_date DESC", nativeQuery = true)
//    List<UserPlan> findByUserIdOrderByStartDateDesc(@Param("userId") String userId);
//	
//	@Query(value = "SELECT COUNT(*) FROM user_plans WHERE user_id = :userId "
//			+ "AND status IN (:statuses)", nativeQuery = true)
//    long countByUserIdAndStatusIn(@Param("userId") String userId, @Param("statuses") List<String> statuses);
//	
//	@Query(value = "SELECT * FROM user_plans WHERE user_id = :userId AND "
//			+ "status = :status LIMIT 1", nativeQuery = true)
//    Optional<UserPlan> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
//
//    
//    List<UserPlan> findByStatusIn(List<PlanStatus> statuses);
    
    

    
}