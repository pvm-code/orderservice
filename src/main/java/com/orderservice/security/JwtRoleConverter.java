package com.orderservice.security;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Component
public class JwtRoleConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String role = jwt.getClaimAsString("role");

        Collection<SimpleGrantedAuthority> authorities =
                role == null || role.isBlank()
                        ? List.of()
                        : List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                        );

        return new JwtAuthenticationToken(jwt, authorities);
    }
}