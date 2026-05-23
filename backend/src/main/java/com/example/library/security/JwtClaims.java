package com.example.library.security;

import com.example.library.enums.UserRole;
import java.time.Instant;

public class JwtClaims {
    private final Long userId;
    private final String username;
    private final UserRole role;
    private final Instant expiresAt;

    public JwtClaims(Long userId, String username, UserRole role, Instant expiresAt) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.expiresAt = expiresAt;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public UserRole getRole() { return role; }
    public Instant getExpiresAt() { return expiresAt; }
}
