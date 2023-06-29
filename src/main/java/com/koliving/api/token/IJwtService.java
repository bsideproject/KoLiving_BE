package com.koliving.api.token;

import org.springframework.security.core.Authentication;

public interface IJwtService {

    String extractEmail(String token);

    Authentication getAuthentication(String accessToken);

    boolean isRefreshTokenPresent(String email);

    void deleteRefreshToken(String email);

    String saveRefreshToken(String email, String newRefreshTokenValue);

}
