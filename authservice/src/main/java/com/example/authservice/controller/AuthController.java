package com.example.authservice.controller;

import com.example.authservice.dto.*;
import com.example.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(registerRequest));
    }

    @GetMapping("/validate-user/{userId}")
    public ResponseEntity<Boolean> validateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.validateUser(userId));
    }

    @GetMapping("/validate-user/{userId}/{role}")
    public ResponseEntity<Boolean> validateRole(@PathVariable Long userId,@PathVariable String role) {
        return ResponseEntity.ok(authService.validateRole(userId,role));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Boolean> validateSecurityCredentials(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.validateSecurityAnswer(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
   
    @GetMapping("/get-all-employees")
    public ResponseEntity<List<UserDTO>> getAllEmployees() {
        return ResponseEntity.ok(authService.getAllEmployees()); 
    }
    @GetMapping("/find-user/{userId}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.findUserById(userId));
    }
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(authService.updateUser(userId, updateUserRequest));
    }
     
}