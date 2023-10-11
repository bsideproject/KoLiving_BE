package com.koliving.api.auth.login;

import com.koliving.api.auth.AuthFacade;
import com.koliving.api.dto.JwtTokenDto;
import com.koliving.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AuthFacade authFacade;
    private final HttpUtils httpUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        JwtTokenDto authTokens = authFacade.issueAuthTokens((UserDetails) authentication.getPrincipal());
        String accessToken = authTokens.getAccessToken();
        String refreshToken = authTokens.getRefreshToken();

        httpUtils.setResponseWithCookies(
                response,
                httpUtils.createSuccessResponse("login success", HttpStatus.OK.value()),
                httpUtils.getCookieOfAccessToken(accessToken),
                httpUtils.getCookieOfRefreshToken(refreshToken)
        );

        response.addHeader(HttpHeaders.AUTHORIZATION, "bearer " + accessToken);
    }
}
