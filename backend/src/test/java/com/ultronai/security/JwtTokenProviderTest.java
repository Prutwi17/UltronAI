package com.ultronai.security;

import com.ultronai.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "c2VjcmV0LWtleS1mb3ItdWx0cm9uYWktand0LWVudGVycHJpc2Utc2VjdXJpdHktc3BlY2lmaWNhdGlvbg==";
    private final long expirationMs = 900000; // 15 mins

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserPrincipal principal = new UserPrincipal(101L, 1L, "Admin User", "admin@tenant1.com", "hash", Role.TENANT_ADMIN, true);

        String token = jwtTokenProvider.generateAccessToken(principal);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("admin@tenant1.com", jwtTokenProvider.getEmailFromToken(token));
        assertEquals(101L, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(1L, jwtTokenProvider.getTenantIdFromToken(token));
        assertEquals(Role.TENANT_ADMIN, jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    void testInvalidTokenRejection() {
        assertFalse(jwtTokenProvider.validateToken("invalid.jwt.token"));
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    void testTokenExpiration() {
        JwtTokenProvider shortExpiryProvider = new JwtTokenProvider(secret, -1000); // Already expired
        UserPrincipal principal = new UserPrincipal(102L, 2L, "Expired User", "expired@tenant2.com", "hash", Role.CUSTOMER, true);

        String token = shortExpiryProvider.generateAccessToken(principal);
        assertFalse(shortExpiryProvider.validateToken(token));
    }
}
