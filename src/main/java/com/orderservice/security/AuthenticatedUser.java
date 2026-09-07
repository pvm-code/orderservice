package com.orderservice.security;

import java.util.UUID;

public class AuthenticatedUser {

    private final UUID userId;
    private final String role;
    private final String email;

    public AuthenticatedUser(UUID userId, String role,String email) {
        this.userId = userId;
        this.role = role;
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    
    public String getEmail() {
		return email;
	}

	public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}