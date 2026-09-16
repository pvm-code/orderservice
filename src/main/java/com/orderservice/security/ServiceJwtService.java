package com.orderservice.security;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class ServiceJwtService {

    private final RSAPrivateKey privateKey;

    public ServiceJwtService(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String createProductServiceToken() {

        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("orderservice")
                .subject("orderservice")
                .audience("productservice")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build(),
                claims
        );

        try {
            signedJWT.sign(new RSASSASigner(privateKey));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException(
                    "Unable to create service JWT", e
            );
        }
    }
}