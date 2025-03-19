package com.example.mock;
 
import com.example.authservice.controller.AuthController;
import com.example.authservice.dto.*;
import com.example.authservice.service.AuthService;
import com.example.authservice.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;
    private MockMvc mockMvc;
    @BeforeEach
     void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }
    // Test for login
    @Test
    void testLogin() throws Exception {
        AuthResponse authResponse = AuthResponse.builder()
                .token("dummyToken")
                .user(UserDTO.builder()
                        .id(1L)
                        .email("user@example.com")
                        .firstName("User")
                        .lastName("Example")
                        .role(UserRole.USER)
                        .build())
                .build();
 
        // Mocking the service method
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/account/login")
                        .contentType("application/json")
                        .content("{\"email\": \"user@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummyToken"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
 
        verify(authService, times(1)).login(any(LoginRequest.class));
    }
 

    // Test for register
    @Test
    void testRegister() throws Exception {
        AuthResponse authResponse = AuthResponse.builder()
                .token("dummyToken")
                .user(UserDTO.builder()
                        .id(1L)
                        .email("user@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .role(UserRole.USER)
                        .build())
                .build();
 

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);
 

        mockMvc.perform(post("/account/register")
                        .contentType("application/json")
                        .content("{\"email\": \"user@example.com\", \"password\": \"password123\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"mobile\": \"1234567890\", \"role\": \"USER\", \"securityQuestion\": \"What is your favorite color?\", \"securityAnswer\": \"Blue\"}"))
                .andExpect(status().isCreated())  // Need to get HTTP status 201
                .andExpect(jsonPath("$.token").value("dummyToken"))  
                .andExpect(jsonPath("$.user.email").value("user@example.com"));  

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }
 
    // Test for validateUser
    @Test
     void testValidateUser() throws Exception {
        when(authService.validateUser(1L)).thenReturn(true);

        mockMvc.perform(get("/account/validate-user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));  
        verify(authService, times(1)).validateUser(1L);
    }

    // Test for validateRole
    @Test
     void testValidateRole() throws Exception {
        when(authService.validateRole(1L, "ADMIN")).thenReturn(true);

        mockMvc.perform(get("/account/validate-user/1/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));  
        verify(authService, times(1)).validateRole(1L, "ADMIN");
    }
    // Test for resetPassword
    @Test
    void testResetPassword() throws Exception {

        mockMvc.perform(post("/account/reset-password")
                        .contentType("application/json")
                        .content("{\"email\": \"user@example.com\", \"newPassword\": \"newPassword123\"}"))
                .andExpect(status().isOk());

        verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }
 

    // Test for getAllEmployees
    @Test
     void testGetAllEmployees() throws Exception {
        List<UserDTO> userDTOList = List.of(
                UserDTO.builder().id(1L).firstName("John").lastName("Doe").email("john@example.com").role(UserRole.USER).build(),
                UserDTO.builder().id(2L).firstName("Jane").lastName("Smith").email("jane@example.com").role(UserRole.ADMIN).build()
        );
        when(authService.getAllEmployees()).thenReturn(userDTOList);
        mockMvc.perform(get("/account/get-all-employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
        verify(authService, times(1)).getAllEmployees();
    }
    // Test for findUserById
    @Test
   void testFindUserById() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .role(UserRole.USER)
                .build();
        when(authService.findUserById(1L)).thenReturn(userDTO);
        mockMvc.perform(get("/account/find-user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
        verify(authService, times(1)).findUserById(1L);
    }
    // Test for updateUser
    @Test
    void testUpdateUser() throws Exception {

        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("newEmail@example.com")
                .role(UserRole.USER)
                .build();

        when(authService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/account/update-user/1")
                        .contentType("application/json")
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"newEmail@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newEmail@example.com"));

        verify(authService, times(1)).updateUser(eq(1L), any(UpdateUserRequest.class));
    }
 
}