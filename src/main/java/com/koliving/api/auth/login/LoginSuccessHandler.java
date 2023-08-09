package com.koliving.api.auth.login;

import com.koliving.api.auth.AuthFacade;
import com.koliving.api.dto.JwtTokenDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AuthFacade authFacade;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public LoginSuccessHandler(AuthFacade authFacade,
                               @Value("${jwt.expiration:2}") int accessTokenValidity,
                               @Value("${jwt.refreshExpiration:30}") int refreshTokenValidity) {
        this.authFacade = authFacade;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        JwtTokenDto authTokens = authFacade.issueAuthTokens((UserDetails) authentication.getPrincipal());

        setResponse(response, getCookieOfAccessToken(authTokens.getAccessToken()), getCookieOfRefreshToken(authTokens.getRefreshToken()));
    }

    private void setResponse(HttpServletResponse response, Cookie accessToken, Cookie refreshToken) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addCookie(accessToken);
        response.addCookie(refreshToken);
    }

    private Cookie getCookieOfAccessToken(String accessTokenValue) {
        Cookie accessToken = new Cookie("access_token", accessTokenValue);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setMaxAge((int) (60 * 60 * accessTokenValidity));

        return accessToken;
    }

    private Cookie getCookieOfRefreshToken(String refreshTokenValue) {
        Cookie refreshToken = new Cookie("refresh_token", refreshTokenValue);
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);
        refreshToken.setMaxAge((int) (60 * 60 * 24 * refreshTokenValidity));

        return refreshToken;
    }
}
