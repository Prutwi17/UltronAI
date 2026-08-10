package com.ultronai.security;

import com.ultronai.model.enums.Role;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
        @Value("${jwt.secret:c2VjcmV0LWtleS1mb3ItdWx0cm9uYWktand0LWVudGVycHJpc2Utc2VjdXJpdHktc3BlY2lmaWNhdGlvbg==}") String secret,
        @Value("${jwt.expiration-ms:900000}") long jwtExpirationMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateAccessToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        JwtBuilder builder = Jwts.builder()
            .subject(userPrincipal.getUsername())
            .claim("userId", userPrincipal.getId())
            .claim("role", userPrincipal.getRole().name())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key);

        if (userPrincipal.getTenantId() != null) {
            builder.claim("tenantId", userPrincipal.getTenantId());
        }

        return builder.compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.get("userId", Long.class);
    }

    public Long getTenantIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.get("tenantId", Long.class);
    }

    public Role getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        String roleStr = claims.get("role", String.class);
        return Role.valueOf(roleStr);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}
