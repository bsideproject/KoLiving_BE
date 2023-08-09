package com.koliving.api.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtProvider {

    private final String jwtSecret;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtProvider(@Value("${jwt.secret:koliving}") String jwtSecret,
                       @Value("${jwt.expiration:2}") int accessTokenValidity,
                       @Value("${jwt.refreshExpiration:30}") int refreshTokenValidity) {
        this.jwtSecret = jwtSecret;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String generateAccessToken(JwtVo jwtVo) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", jwtVo.getEmail());
        payloads.put("role", jwtVo.getRole());

        return generateJwtBuilder(payloads)
                .setSubject("Access Token (" + jwtVo.getUsername() + ")")
                .setExpiration(calculateExpiryDate(accessTokenValidity))
                .compact();
    }

    public String generateRefreshToken(JwtVo jwtVo) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", jwtVo.getEmail());

        return generateJwtBuilder(payloads)
                .setSubject("Refresh Token (" + jwtVo.getEmail() + ")")
                .setExpiration(calculateExpiryDate(refreshTokenValidity * 24))
                .compact();
    }

    public void validateToken(String token) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);

        try {
            Jwts.parser()
                .setSigningKey(apiKeySecretBytes)
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.error("access token has expired");
            throw new JwtException("expired_token");
        } catch (MalformedJwtException e) {
            log.error("malformed jwt token");
            throw new JwtException("malformed_token");
        } catch (SignatureException e) {
            log.error("signature validation failed");
            throw new JwtException("signature_invalid_token");
        } catch (UnsupportedJwtException e) {
            log.error("unexpected format of jwt token");
            throw new JwtException("format_invalid_token");
        }
    }

    private JwtBuilder generateJwtBuilder(Map<String, Object> payloads) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 서명에 담을 데이터
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        Key signKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(signatureAlgorithm, signKey);
    }

    private Date calculateExpiryDate(long validityHour) {
        return Date.from(Instant.now().plus(validityHour, ChronoUnit.HOURS));
    }
}
