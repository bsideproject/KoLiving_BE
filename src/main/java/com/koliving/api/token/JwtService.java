package com.koliving.api.token;

import com.koliving.api.token.refresh.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class JwtService implements IJwtService {

    private String jwtSecret;
    private final UserDetailsService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(@Value("${jwt.secret}") String jwtSecret,
                      UserDetailsService userService,
                      RefreshTokenRepository refreshTokenRepository) {
        this.jwtSecret = jwtSecret;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String extractEmail(String token) {
        Claims claim = getClaims(token);
        return (String) claim.get("email");
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        return null;
    }

    @Override
    public String saveRefreshToken(String email, String newRefreshTokenValue) {
        return null;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                .parseClaimsJws(token)
                .getBody();
    }
}
