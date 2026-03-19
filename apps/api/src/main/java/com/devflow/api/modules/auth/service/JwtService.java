package com.devflow.api.modules.auth.service;

import com.devflow.api.common.security.PrincipalType;
import com.devflow.api.modules.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_PRINCIPAL_TYPE = "principalType";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public IssuedToken issueAccessToken(Long subjectId, PrincipalType principalType, String role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getAccessTokenMinutes(), ChronoUnit.MINUTES);
        String token = buildToken(subjectId, principalType, role, TOKEN_TYPE_ACCESS, now, expiresAt);
        return new IssuedToken(token, expiresAt);
    }

    public IssuedToken issueRefreshToken(Long subjectId, PrincipalType principalType, String role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getRefreshTokenDays(), ChronoUnit.DAYS);
        String token = buildToken(subjectId, principalType, role, TOKEN_TYPE_REFRESH, now, expiresAt);
        return new IssuedToken(token, expiresAt);
    }

    public JwtPayload parseAccessToken(String token) throws JwtException {
        return parseToken(token, TOKEN_TYPE_ACCESS);
    }

    public JwtPayload parseRefreshToken(String token) throws JwtException {
        return parseToken(token, TOKEN_TYPE_REFRESH);
    }

    private JwtPayload parseToken(String token, String expectedTokenType) throws JwtException {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            if (!expectedTokenType.equals(tokenType)) {
                throw new JwtException("Unexpected token type");
            }

            return new JwtPayload(
                    Long.parseLong(claims.getSubject()),
                    PrincipalType.valueOf(claims.get(CLAIM_PRINCIPAL_TYPE, String.class)),
                    claims.get(CLAIM_ROLE, String.class),
                    Instant.ofEpochMilli(claims.getIssuedAt().getTime()),
                    Instant.ofEpochMilli(claims.getExpiration().getTime())
            );
        } catch (ExpiredJwtException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new JwtException("Invalid token", exception);
        }
    }

    private String buildToken(Long subjectId,
                              PrincipalType principalType,
                              String role,
                              String tokenType,
                              Instant issuedAt,
                              Instant expiresAt) {
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(subjectId))
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_PRINCIPAL_TYPE, principalType.name())
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public record IssuedToken(String token, Instant expiresAt) {
    }

    public record JwtPayload(
            Long subjectId,
            PrincipalType principalType,
            String role,
            Instant issuedAt,
            Instant expiresAt
    ) {
    }
}
