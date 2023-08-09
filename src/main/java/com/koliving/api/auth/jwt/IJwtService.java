package com.koliving.api.auth.jwt;

import org.springframework.security.core.Authentication;

import java.util.Date;

public interface IJwtService {

    Date extractExpirationDate(String token);

    String extractEmail(String token);

    Authentication createAuthentication(String accessToken);

    boolean isBlackList(String accessToken);

    void registerBlackList(String accessToken, Date expirationDate);

    String getRefreshToken(String email);

    boolean isExistsRefreshToken(String email);

    void deleteRefreshToken(String email);

    String saveRefreshToken(String email, String newRefreshTokenValue);
}
