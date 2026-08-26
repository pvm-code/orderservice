package com.orderservice.security;

import java.util.UUID;

public class AuthenticatedUser {

    private final UUID userId;
    private final String role;

    public AuthenticatedUser(UUID userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}