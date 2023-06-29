package com.koliving.api.token;

import org.springframework.security.core.Authentication;

import java.util.Date;

public interface IJwtService {

    Date extractExpirationDate(String token);

    String extractEmail(String token);

    Authentication getAuthentication(String accessToken);

    boolean isBlackList(String accessToken);

    void registerBlackList(String accessToken, Date expirationDate);

    boolean isRefreshTokenPresent(String email);

    void deleteRefreshToken(String email);

    String saveRefreshToken(String email, String newRefreshTokenValue);
}
