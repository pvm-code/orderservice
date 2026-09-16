package com.orderservice.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceKeyConfig {

    @Value("${service.jwt.private-key}")
    private String privateKey;

    @Bean
    public RSAPrivateKey servicePrivateKey() {

        try {
            String key = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(
                    key.getBytes(StandardCharsets.UTF_8)
            );

            PKCS8EncodedKeySpec keySpec =
                    new PKCS8EncodedKeySpec(decoded);

            KeyFactory keyFactory =
                    KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to load Order Service private key", e
            );
        }
    }
}