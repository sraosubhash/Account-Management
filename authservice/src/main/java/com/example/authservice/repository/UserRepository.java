package com.example.authservice.repository;

import com.example.authservice.model.User;
import com.example.authservice.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    Optional<List<User>> findByRole(UserRole role);
    boolean existsByEmail(String email);
	boolean existsByMobile(String mobile);
	
    
}