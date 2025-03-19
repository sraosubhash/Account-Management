package com.example.authservice.dto;

import com.example.authservice.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 10, max = 10)
    private String mobile;
    
    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private String alternatePhone;
    private String address;
    private UserRole role;
    @NotBlank
    private String securityQuestion;
    @NotBlank
    private String securityAnswer;
}