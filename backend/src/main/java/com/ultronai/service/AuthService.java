package com.ultronai.service;

import com.ultronai.dto.request.LoginRequest;
import com.ultronai.dto.request.RefreshTokenRequest;
import com.ultronai.dto.request.RegisterRequest;
import com.ultronai.dto.response.AuthResponse;
import com.ultronai.dto.response.TenantResponse;
import com.ultronai.dto.response.UserResponse;
import com.ultronai.exception.DuplicateResourceException;
import com.ultronai.exception.InvalidTokenException;
import com.ultronai.exception.UnauthorizedException;
import com.ultronai.model.entity.RefreshToken;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.User;
import com.ultronai.model.enums.Role;
import com.ultronai.model.enums.TenantStatus;
import com.ultronai.repository.RefreshTokenRepository;
import com.ultronai.repository.TenantRepository;
import com.ultronai.repository.UserRepository;
import com.ultronai.security.JwtTokenProvider;
import com.ultronai.security.UserPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
        UserRepository userRepository,
        TenantRepository tenantRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider,
        AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, String ipAddress) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with email '" + request.getEmail() + "' already exists");
        }

        if (tenantRepository.existsBySlug(request.getTenantSlug())) {
            throw new DuplicateResourceException("Tenant slug '" + request.getTenantSlug() + "' is already taken");
        }

        // Untrusted clients cannot self-register as PLATFORM_ADMIN
        Role assignedRole = request.getRole();
        if (assignedRole == Role.PLATFORM_ADMIN) {
            assignedRole = Role.TENANT_ADMIN;
        }

        // Create Tenant
        Tenant tenant = new Tenant(request.getTenantName(), request.getTenantSlug());
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant = tenantRepository.save(tenant);

        // Create User with hashed password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(tenant, request.getFullName(), request.getEmail(), hashedPassword, assignedRole);
        user = userRepository.save(user);

        // Log audit event
        auditLogService.logSecurityEvent(tenant.getId(), user.getId(), "REGISTRATION", "User", user.getId().toString(), ipAddress);

        // Generate Tokens
        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = createAndSaveRefreshToken(user);

        return buildAuthResponse(accessToken, refreshToken, user, tenant);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                auditLogService.logSecurityEvent(null, null, "LOGIN_FAILURE", "Auth", request.getEmail(), ipAddress);
                return new BadCredentialsException("Invalid email or password");
            });

        if (!user.isActive()) {
            auditLogService.logSecurityEvent(user.getTenant() != null ? user.getTenant().getId() : null, user.getId(), "LOGIN_FAILURE_DISABLED", "User", user.getId().toString(), ipAddress);
            throw new UnauthorizedException("Account is disabled. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            auditLogService.logSecurityEvent(user.getTenant() != null ? user.getTenant().getId() : null, user.getId(), "LOGIN_FAILURE", "User", user.getId().toString(), ipAddress);
            throw new BadCredentialsException("Invalid email or password");
        }

        // Revoke old refresh tokens
        refreshTokenRepository.revokeAllByUser(user);

        // Log audit event
        Long tenantId = user.getTenant() != null ? user.getTenant().getId() : null;
        auditLogService.logSecurityEvent(tenantId, user.getId(), "LOGIN_SUCCESS", "User", user.getId().toString(), ipAddress);

        // Generate Tokens
        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = createAndSaveRefreshToken(user);

        return buildAuthResponse(accessToken, refreshToken, user, user.getTenant());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request, String ipAddress) {
        String rawToken = request.getRefreshToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException("Invalid or revoked refresh token"));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Expired or revoked refresh token");
        }

        User user = storedToken.getUser();
        if (!user.isActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        // Revoke used token and issue new token pair
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        Long tenantId = user.getTenant() != null ? user.getTenant().getId() : null;
        auditLogService.logSecurityEvent(tenantId, user.getId(), "REFRESH_TOKEN", "User", user.getId().toString(), ipAddress);

        UserPrincipal principal = UserPrincipal.create(user);
        String newAccessToken = jwtTokenProvider.generateAccessToken(principal);
        String newRefreshToken = createAndSaveRefreshToken(user);

        return buildAuthResponse(newAccessToken, newRefreshToken, user, user.getTenant());
    }

    @Transactional
    public void logout(UserPrincipal principal, String ipAddress) {
        if (principal != null) {
            User user = userRepository.findById(principal.getId()).orElse(null);
            if (user != null) {
                refreshTokenRepository.revokeAllByUser(user);
                auditLogService.logSecurityEvent(principal.getTenantId(), user.getId(), "LOGOUT", "User", user.getId().toString(), ipAddress);
            }
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        return mapToUserResponse(user);
    }

    private String createAndSaveRefreshToken(User user) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String tokenHash = hashToken(rawToken);

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        RefreshToken refreshToken = new RefreshToken(user, tokenHash, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user, Tenant tenant) {
        UserResponse userResponse = mapToUserResponse(user);
        TenantResponse tenantResponse = tenant != null ? mapToTenantResponse(tenant) : null;
        return new AuthResponse(
            accessToken,
            refreshToken,
            jwtTokenProvider.getExpirationMs(),
            userResponse,
            tenantResponse
        );
    }

    private UserResponse mapToUserResponse(User user) {
        Long tenantId = user.getTenant() != null ? user.getTenant().getId() : null;
        return new UserResponse(
            user.getId(),
            tenantId,
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            user.isActive(),
            user.getCreatedAt()
        );
    }

    private TenantResponse mapToTenantResponse(Tenant tenant) {
        return new TenantResponse(
            tenant.getId(),
            tenant.getName(),
            tenant.getSlug(),
            tenant.getStatus(),
            tenant.getCreatedAt()
        );
    }
}
