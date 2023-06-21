package com.koliving.api.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:24}")
    private long expiration;

    public String generate(JwtVo jwtVo) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        String email = jwtVo.getEmail();
        String role = jwtVo.getRole();
        String username = jwtVo.getUsername();

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", email);
        payloads.put("role", role);

        Date createdAt = new Date();
        Date expiredAt = new Date();

        // 유효 기간 = ms * sec * min * {hour}
        Long validity = 1000 * 60L * 60L * expiration;
        expiredAt.setTime(createdAt.getTime() + validity);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 서명에 담을 데이터
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        String jwt = Jwts.builder()
                .setSubject("user-auth " + username)
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(createdAt)
                .setExpiration(expiredAt)
                .signWith(signatureAlgorithm, signKey)
                .compact();

        return jwt;
    }
}
