package com.ultronai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.dto.request.LoginRequest;
import com.ultronai.dto.request.RegisterRequest;
import com.ultronai.dto.response.AuthResponse;
import com.ultronai.dto.response.TenantResponse;
import com.ultronai.dto.response.UserResponse;
import com.ultronai.model.enums.Role;
import com.ultronai.model.enums.TenantStatus;
import com.ultronai.security.JwtAuthenticationFilter;
import com.ultronai.security.JwtTokenProvider;
import com.ultronai.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testRegisterEndpoint() throws Exception {
        RegisterRequest request = new RegisterRequest("Test Admin", "admin@test.com", "Password123!", "Test Tenant", "test-tenant");
        UserResponse userResponse = new UserResponse(1L, 100L, "Test Admin", "admin@test.com", Role.TENANT_ADMIN, true, LocalDateTime.now());
        TenantResponse tenantResponse = new TenantResponse(100L, "Test Tenant", "test-tenant", TenantStatus.ACTIVE, LocalDateTime.now());
        AuthResponse authResponse = new AuthResponse("access.jwt.token", "refresh.raw.token", 900000L, userResponse, tenantResponse);

        when(authService.register(any(), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").value("access.jwt.token"))
            .andExpect(jsonPath("$.user.email").value("admin@test.com"))
            .andExpect(jsonPath("$.tenant.slug").value("test-tenant"));
    }

    @Test
    void testLoginEndpoint() throws Exception {
        LoginRequest request = new LoginRequest("admin@test.com", "Password123!");
        UserResponse userResponse = new UserResponse(1L, 100L, "Test Admin", "admin@test.com", Role.TENANT_ADMIN, true, LocalDateTime.now());
        TenantResponse tenantResponse = new TenantResponse(100L, "Test Tenant", "test-tenant", TenantStatus.ACTIVE, LocalDateTime.now());
        AuthResponse authResponse = new AuthResponse("access.jwt.token", "refresh.raw.token", 900000L, userResponse, tenantResponse);

        when(authService.login(any(), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access.jwt.token"));
    }

    @Test
    void testInvalidRegisterPayloadRejection() throws Exception {
        RegisterRequest request = new RegisterRequest("", "invalid-email", "short", "", "Invalid Slug!");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
