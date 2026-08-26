package com.orderservice.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.security.JwtAccessDeniedHandler;
import com.orderservice.security.JwtAuthenticationEntryPoint;
import com.orderservice.security.JwtRoleConverter;

import io.jsonwebtoken.security.Keys;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    
    
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtRoleConverter jwtRoleConverter;

    

    public SecurityConfig( JwtAuthenticationEntryPoint authenticationEntryPoint,
			JwtAccessDeniedHandler accessDeniedHandler, JwtRoleConverter jwtRoleConverter) {
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
		this.jwtRoleConverter = jwtRoleConverter;
	}

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                .requestMatchers("/api/v1/order/**").hasRole("USER")

                .anyRequest().authenticated()
                
            ).exceptionHandling(exception ->exception
            		
            		.authenticationEntryPoint(authenticationEntryPoint)
            		.accessDeniedHandler(accessDeniedHandler)
            		
            		)
            
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> 
                jwt.jwtAuthenticationConverter(jwtRoleConverter))
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        SecretKey key = Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );

        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}