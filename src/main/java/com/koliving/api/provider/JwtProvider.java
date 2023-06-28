package com.koliving.api.provider;

import com.koliving.api.clock.IClock;
import com.koliving.api.token.refresh.RefreshToken;
import com.koliving.api.token.refresh.RefreshTokenRepository;
import com.koliving.api.vo.JwtVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
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

    @Transactional
    public String saveRefreshToken(String email) {
        RefreshToken newRefreshToken = RefreshToken.builder()
                .email(email)
                .build();

        return refreshTokenRepository.save(newRefreshToken).getRefreshToken();
    }

    public boolean validateAccessToken(String token) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);

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
        } catch (SignatureException e){
            log.error("signature validation failed");
            throw new SignatureException("signature validation failed");
        } catch (UnsupportedJwtException e){
            log.error("unexpected format of jwt token");
            throw new UnsupportedJwtException("unexpected format of jwt token");
        } catch (Exception e) {

        }

        return false;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        Claims claim = getClaimsFormToken(token);
        return (String) claim.get("email");
    }

    private Claims getClaimsFormToken(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parseClaimsJws(token)
                .getBody();
    }
}
