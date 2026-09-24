package com.orderservice.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import com.orderservice.security.JwtAccessDeniedHandler;
import com.orderservice.security.JwtAuthenticationEntryPoint;
import com.orderservice.security.JwtRoleConverter;

import io.jsonwebtoken.security.Keys;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtRoleConverter jwtRoleConverter;

    public SecurityConfig(
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            JwtAccessDeniedHandler accessDeniedHandler,
            JwtRoleConverter jwtRoleConverter) {

        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jwtRoleConverter = jwtRoleConverter;
    }

    /*
     * ============================================================
     * INTERNAL SERVICE SECURITY
     *
     * Payment Service -> Order Service
     *
     * Authentication:
     * Keycloak JWT
     *
     * Required scope:
     * internal:order:read
     * ============================================================
     */
    @Bean
    @Order(1)
    public SecurityFilterChain internalSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("keycloakJwtDecoder")
            JwtDecoder keycloakJwtDecoder) throws Exception {

        http
            .securityMatcher("/internal/orders/**")

            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/orders/**")
                .hasAuthority("SCOPE_internal:order:read")

                .anyRequest()
                .denyAll()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.decoder(keycloakJwtDecoder)
                )
            );

        return http.build();
    }

    /*
     * ============================================================
     * USER-FACING SECURITY
     *
     * Browser/User Service -> Order Service
     *
     * Existing User Service JWT remains unchanged.
     * ============================================================
     */
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("jwtDecoder") JwtDecoder jwtDecoder) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info"
                )
                .permitAll()

                .anyRequest()
                .authenticated()
            )

            .exceptionHandling(exception ->
                exception
                    .authenticationEntryPoint(
                        authenticationEntryPoint
                    )
                    .accessDeniedHandler(
                        accessDeniedHandler
                    )
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                        jwtRoleConverter
                    )
                )
            );

        return http.build();
    }

    /*
     * ============================================================
     * EXISTING USER JWT DECODER
     *
     * Do NOT remove this.
     * ============================================================
     */
    @Bean
    @Order(2)
    public JwtDecoder jwtDecoder() {

        SecretKey key = Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );

        return NimbusJwtDecoder
            .withSecretKey(key)
            .build();
    }
}