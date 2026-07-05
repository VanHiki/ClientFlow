package com.clientflow.backend.security;

import com.clientflow.backend.domain.user.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtService {

    @Value("${clientflow.jwt.secret}")
    private String jwtSecret;

    @Value("${clientflow.jwt.expiration-seconds}")
    private long expirationSeconds;

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationSeconds, ChronoUnit.SECONDS);
        String role = user.getRole().getName().name();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("clientflow")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiresAt))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("role", role)
                .claim("scope", "ROLE_" + role)
                .build();

        JWSObject jwsObject = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS256),
                new Payload(claimsSet.toJSONObject())
        );

        try {
            jwsObject.sign(new MACSigner(jwtSecret.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException exception) {
            log.error("Cannot create JWT token", exception);
            throw new IllegalStateException("Cannot create JWT token", exception);
        }
    }
}
