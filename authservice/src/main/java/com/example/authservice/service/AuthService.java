package com.example.authservice.service;

import com.example.authservice.dto.*;
import com.example.authservice.exception.BadRequestException;
import com.example.authservice.exception.ResourceNotFoundException;
import com.example.authservice.model.User;
import com.example.authservice.model.UserRole;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.JwtTokenProvider;
import org.apache.log4j.Logger;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service

@RequiredArgsConstructor
public class AuthService {

   
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private static final Logger log = Logger.getLogger(AuthService.class);
    /**
     * Handles user login, authenticates and returns a JWT token
     * 
     * @param loginRequest login request containing email and password
     * @return AuthResponse containing JWT token and user data
     * @throws ResourceNotFoundException if the user is not found
     */
    public AuthResponse login(LoginRequest loginRequest) {
    	if (loginRequest.getMobile() != null) {
    	    userRepository.findByMobile(loginRequest.getMobile())
    	            .ifPresent(user -> loginRequest.setEmail(user.getEmail()));
    	}

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        return AuthResponse.builder()
                .token(jwt)
                .user(modelMapper.map(user, UserDTO.class))
                .build();
    }
    
    
    /**
     * Registers a new user and sends a welcome email after registration.
     *
     * @param registerRequest the request containing user details
     * @return AuthResponse containing JWT token and user data
     * @throws BadRequestException if the email or mobile already exists
     */
 

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByMobile(registerRequest.getMobile())) {
            throw new BadRequestException("Mobile already registered");
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .mobile(registerRequest.getMobile())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .alternatePhone(registerRequest.getAlternatePhone())
                .address(registerRequest.getAddress())
                .role(registerRequest.getRole())
                .securityQuestion(registerRequest.getSecurityQuestion())
                .securityAnswer(registerRequest.getSecurityAnswer())
                .build();

        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully: {}"+ savedUser.getEmail());
        String subject ="Welcome to FutureWave – Your Registration is Complete!";
        String message = "Hello " + registerRequest.getFirstName() + ",<br><br>"
                + "Thank you for registering with us! You’ve successfully created an account. <br><br>"
                + "Registered email: " + registerRequest.getEmail() + "<br>"
                +"Explore our features and start using <i>FutureWave</i> for best services.<br>"
                +"If you need help getting started or have any questions, don’t hesitate to contact our support team. We’re here to help!</p>"
                + "<p>We’re thrilled to have you as a part of our community. We hope you enjoy your experience with us!</p>"
                + "Best Regards,<br><i>FutureWave</i> </br>"
        		+"futurewavehelp@gmail.com";

        emailService.sendEmail(registerRequest.getEmail(), subject, message);
        
        try {
            log.info("Attempting to send welcome email to: {}"+ savedUser.getEmail());
            emailService.sendEmail(savedUser.getEmail(), subject, message);
            log.info("Welcome email sent successfully to: {}"+ savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}" + savedUser.getEmail(), e);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(jwt)
                .user(modelMapper.map(savedUser, UserDTO.class))
                .build();
    }
    
    /**
     * Validates if the user exists by ID.
     *
     * @param userId the user ID to check
     * @return true if the user exists, otherwise false
     */

    public boolean validateUser(Long userId) {
        return userRepository.existsById(userId);
    }
    
    /**
     * Validates if the user has the specified role.
     *
     * @param userId the user ID
     * @param role the role to validate against
     * @return true if the user has the specified role, false otherwise
     */

    public boolean validateRole(Long userId,String role) {
        Optional<User> user=userRepository.findById(userId);
        if(user.isPresent()) {
            String userRole=user.get().getRole().toString();
            return role.equalsIgnoreCase(userRole);
        }
        return false;
    }
    
    /**
     * Validates the security question and answer for password reset.
     *
     * @param request the request containing email, security question, and answer
     * @return true if the security question and answer match, otherwise false
     */

    public boolean validateSecurityAnswer(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        return user.getSecurityQuestion().equals(request.getSecurityQuestion()) &&
                user.getSecurityAnswer().equals(request.getSecurityAnswer());
    }
    
    /**
     * Validates the security question and answer for password reset.
     *
     * @param request the request containing email, security question, and answer
     * @return true if the security question and answer match, otherwise false
     */

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Validate security question and answer
        if (!validateSecurityAnswer(ForgotPasswordRequest.builder()
                .email(request.getEmail())
                .securityQuestion(request.getSecurityQuestion())
                .securityAnswer(request.getSecurityAnswer())
                .build())) {
            throw new BadRequestException("Invalid security question or answer");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Retrieves all users with the EMPLOYEE role
     * @return List of UserDTO objects representing employees
     */
    public List<UserDTO> getAllEmployees() {
        Optional<List<User>> users = userRepository.findByRole(UserRole.EMPLOYEE);
        List<UserDTO> userDTOS = new ArrayList<>();

        // Use ifPresent to avoid calling get() directly
        users.ifPresent(list -> list.forEach(user -> 
            userDTOS.add(modelMapper.map(user, UserDTO.class))
        ));
        
        return userDTOS;
    }


    
    /**
    * Find a user by their ID
    * @param userId the ID of the user to find
    * @return UserDTO containing the user's information
    * @throws ResourceNotFoundException if user is not found
    */
   public UserDTO findUserById(Long userId) {
       User user = userRepository.findById(userId)
           .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
       return convertToDTO(user);
   }

   /**
    * Convert a User entity to UserDTO
    * @param user the User entity to convert
    * @return UserDTO containing the user's information
    */
   public UserDTO convertToDTO(User user) {
       return modelMapper.map(user, UserDTO.class);
   }

   // You can remove or keep these methods depending on if they're used elsewhere
   public Optional<User> getUserInfo(Long userId) {
       return userRepository.findById(userId);
   }

   public Long getAuthenticatedUserId() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
           throw new ResourceNotFoundException("User", "authentication", "not found");
       }
       return ((UserPrincipal) authentication.getPrincipal()).getId();
   }
   
   public UserDTO updateUser(Long userId, UpdateUserRequest updateUserRequest) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

       if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().equals(user.getEmail())) {
           if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
               throw new BadRequestException("Email already registered");
           }
           user.setEmail(updateUserRequest.getEmail());
       }

       if (updateUserRequest.getMobile() != null && !updateUserRequest.getMobile().equals(user.getMobile())) {
           if (userRepository.existsByMobile(updateUserRequest.getMobile())) {
               throw new BadRequestException("Mobile already registered");
           }
           user.setMobile(updateUserRequest.getMobile());
       }

       user.setFirstName(updateUserRequest.getFirstName());
       user.setLastName(updateUserRequest.getLastName());
       user.setAlternatePhone(updateUserRequest.getAlternatePhone());
       user.setAddress(updateUserRequest.getAddress());

       User updatedUser = userRepository.save(user);
       return convertToDTO(updatedUser);
   }

    
}