package com.koliving.api.provider;

import com.koliving.api.clock.IClock;
import com.koliving.api.vo.JwtVo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtProvider {

    private String jwtSecret;
    private long expiration;
    private IClock clock;

    public JwtProvider(@Value("${jwt.secret}") String jwtSecret,
                       @Value("${jwt.expiration:24}") long expiration,
                       IClock clock) {
        this.jwtSecret=jwtSecret;
        this.expiration = expiration;
        this.clock = clock;
    }

    public String generateAccessToken(JwtVo jwtVo) {
        String email = jwtVo.getEmail();
        String role = jwtVo.getRole();
        String username = jwtVo.getUsername();

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", email);
        payloads.put("role", role);

        LocalDateTime now = clock.now();
        Date expiredAt = Date.from(now.toInstant(ZoneOffset.UTC));

        // 유효 기간 = ms * sec * min * {hour}
        Long validityTime = 1000 * 60L * 60L * expiration;
        expiredAt.setTime(expiredAt.getTime() + validityTime);

        return generateJwtBuilder(payloads)
                .setSubject("Access Token (" + username + ")")
                .setExpiration(expiredAt)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", email);

        return generateJwtBuilder(payloads)
                .compact();
    }

    public boolean validateToken(String token) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);

        try {
            // 토큰에서 페이로드(Claim) 추출
            // 토큰의 유효성 검증 목적
            Jwts.parser()
                .setSigningKey(apiKeySecretBytes)
                .parseClaimsJws(token)
                .getBody();

            return true;
        } catch (ExpiredJwtException e) {
            log.error("access token has expired");
            throw new ExpiredJwtException(null, null, "access token has expired");
        } catch (MalformedJwtException e) {
            log.error("malformed jwt token");
            throw new MalformedJwtException("malformed jwt token");
        } catch (SignatureException e) {
            log.error("signature validation failed");
            throw new SignatureException("signature validation failed");
        } catch (UnsupportedJwtException e) {
            log.error("unexpected format of jwt token");
            throw new UnsupportedJwtException("unexpected format of jwt token");
        } catch (Exception e) {

        }

        return false;
    }

    private JwtBuilder generateJwtBuilder(Map<String, Object> payloads) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        LocalDateTime now = clock.now();
        Date createdAt = Date.from(now.toInstant(ZoneOffset.UTC));

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 서명에 담을 데이터
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        Key signKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(createdAt)
                .signWith(signatureAlgorithm, signKey);
    }
}
