package com.example.authservice.dto;

import com.example.authservice.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String mobile;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String alternatePhone;
    private String address;
}
