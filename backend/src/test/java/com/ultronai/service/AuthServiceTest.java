package com.ultronai.service;

import com.ultronai.dto.request.LoginRequest;
import com.ultronai.dto.request.RegisterRequest;
import com.ultronai.dto.response.AuthResponse;
import com.ultronai.exception.DuplicateResourceException;
import com.ultronai.model.entity.RefreshToken;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.User;
import com.ultronai.model.enums.Role;
import com.ultronai.model.enums.TenantStatus;
import com.ultronai.repository.RefreshTokenRepository;
import com.ultronai.repository.TenantRepository;
import com.ultronai.repository.UserRepository;
import com.ultronai.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private Tenant testTenant;
    private User testUser;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant("Acme Corp", "acme-corp");
        testTenant.setId(1L);
        testTenant.setStatus(TenantStatus.ACTIVE);

        testUser = new User(testTenant, "Jane Doe", "jane@acme.com", "$2a$12$hashedPassword", Role.TENANT_ADMIN);
        testUser.setId(10L);
    }

    @Test
    void testSuccessfulRegistration() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@acme.com", "Password123!", "Acme Corp", "acme-corp");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(tenantRepository.existsBySlug(request.getTenantSlug())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(testTenant);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$12$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("mock.jwt.token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(900000L);

        AuthResponse response = authService.register(request, "127.0.0.1");

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getAccessToken());
        assertEquals("jane@acme.com", response.getUser().getEmail());
        assertEquals("acme-corp", response.getTenant().getSlug());

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDuplicateEmailRegistrationRejection() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@acme.com", "Password123!", "Acme Corp", "acme-corp");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request, "127.0.0.1"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSuccessfulLogin() {
        LoginRequest request = new LoginRequest("jane@acme.com", "Password123!");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPasswordHash())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("mock.jwt.token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(900000L);

        AuthResponse response = authService.login(request, "127.0.0.1");

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getAccessToken());
        verify(refreshTokenRepository).revokeAllByUser(testUser);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testInvalidPasswordLoginRejection() {
        LoginRequest request = new LoginRequest("jane@acme.com", "WrongPassword!");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPasswordHash())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request, "127.0.0.1"));
        verify(refreshTokenRepository, never()).save(any());
    }
}
