package com.ultronai.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SsrfProtectionGuardTest {

    private SsrfProtectionGuard ssrfGuardStrict;
    private SsrfProtectionGuard ssrfGuardDev;

    @BeforeEach
    void setUp() {
        ssrfGuardStrict = new SsrfProtectionGuard(false);
        ssrfGuardDev = new SsrfProtectionGuard(true);
    }

    @Test
    void testValidPublicUrl() {
        assertDoesNotThrow(() -> ssrfGuardStrict.validateUrl("https://api.github.com/users"));
    }

    @Test
    void testInvalidSchemeFile() {
        SecurityException ex = assertThrows(SecurityException.class, () ->
            ssrfGuardStrict.validateUrl("file:///etc/passwd")
        );
        assertTrue(ex.getMessage().contains("Only HTTP and HTTPS protocols are allowed"));
    }

    @Test
    void testBlockedMetadataHostname() {
        SecurityException ex = assertThrows(SecurityException.class, () ->
            ssrfGuardStrict.validateUrl("http://metadata.google.internal/computeMetadata/v1/")
        );
        assertTrue(ex.getMessage().contains("forbidden"));
    }

    @Test
    void testLocalhostBlockedInStrictProductionMode() {
        SecurityException ex = assertThrows(SecurityException.class, () ->
            ssrfGuardStrict.validateUrl("http://127.0.0.1/admin")
        );
        assertTrue(ex.getMessage().contains("blacklisted IP"));
    }

    @Test
    void testLocalhostAllowedInDevMode() {
        assertDoesNotThrow(() -> ssrfGuardDev.validateUrl("http://127.0.0.1:8080/mock-api"));
    }
}
