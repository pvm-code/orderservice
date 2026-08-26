package com.orderservice.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public AuthenticatedUser get(Authentication authentication) {

        JwtAuthenticationToken jwtAuthentication =
                (JwtAuthenticationToken) authentication;

        String userId = jwtAuthentication
                .getToken()
                .getClaimAsString("userId");

        String role = jwtAuthentication
                .getToken()
                .getClaimAsString("role");
        

        System.out.println("JWT USER ID = " + userId);
        System.out.println("JWT ROLE = " + role);

        return new AuthenticatedUser(
                UUID.fromString(userId),
                role
        );
    }
}