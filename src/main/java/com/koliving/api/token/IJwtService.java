package com.koliving.api.token;

import org.springframework.security.core.Authentication;

public interface IJwtService {

    String extractEmail(String token);

    Authentication getAuthentication(String accessToken);

    String saveRefreshToken(String email, String newRefreshTokenValue);

}
