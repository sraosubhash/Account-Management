package com.example.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @Email
    private String email;
    private String mobile;
    @NotBlank
    @Size(max = 50)
    private String firstName;
    @NotBlank
    @Size(max = 50)
    private String lastName;
    private String alternatePhone;
    private String address;
}