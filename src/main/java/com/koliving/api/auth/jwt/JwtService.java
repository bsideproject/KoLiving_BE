package com.koliving.api.auth.jwt;

import com.koliving.api.token.blacklist.BlackAccessToken;
import com.koliving.api.token.blacklist.BlackListRepository;
import com.koliving.api.token.refresh.RefreshToken;
import com.koliving.api.token.refresh.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService implements IJwtService {

    private final UserDetailsService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final String jwtSecret;

    public JwtService(UserDetailsService userService,
                      RefreshTokenRepository refreshTokenRepository,
                      BlackListRepository blackListRepository,
                      @Value("${jwt.secret}") String jwtSecret) {
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.blackListRepository = blackListRepository;
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Date extractExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    @Override
    public String extractEmail(String token) {
        return (String) getClaims(token).get("email");
    }

    @Override
    public Authentication createAuthentication(String accessToken) {
        UserDetails userDetails = userService.loadUserByUsername(this.extractEmail(accessToken));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public boolean isBlackList(String accessToken) {
        return blackListRepository.existByToken(accessToken);
    }

    @Override
    public void registerBlackList(String accessToken, Date expirationDate) {
        BlackAccessToken blackAccessToken = BlackAccessToken.builder()
                .accessToken(accessToken)
                .expirationTime(expirationDate)
                .build();

        blackListRepository.save(blackAccessToken);
    }

    @Override
    public boolean isExistsRefreshToken(String email) {
        return refreshTokenRepository.existByEmail(email);
    }

    @Override
    public String getRefreshToken(String email) {
        return refreshTokenRepository.findByEmail(email);
    }

    @Override
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.delete(email);
    }

    @Override
    public String saveRefreshToken(String email, String newRTValue) {
        RefreshToken newRT = RefreshToken.builder()
                .email(email)
                .token(newRTValue)
                .build();

        return refreshTokenRepository.save(newRT);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                .parseClaimsJws(token)
                .getBody();
    }
}
