package com.koliving.api.auth.login;

import com.koliving.api.auth.AuthFacade;
import com.koliving.api.dto.JwtTokenDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.user.domain.User;
import com.koliving.api.utils.HttpUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static com.koliving.api.user.UserUtils.createAuthentication;
import static com.koliving.api.user.UserUtils.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginSuccessHandlerTest {

    @Mock
    private AuthFacade authFacade;

    @Mock
    private HttpUtils httpUtils;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LoginSuccessHandler loginSuccessHandler;

    @Test
    @DisplayName("onAuthenticationSuccess() 성공 : 인증 및 응답 확인")
    void onAuthenticationSuccess_success() throws IOException {
        String dummyEmail = "test@koliving.com";
        String dummyPassword = "KolivingPwd12";
        User newUser = createUser(dummyEmail, dummyPassword);

        String accessToken = "access_token_value";
        String refreshToken = "refresh_token_value";
        JwtTokenDto jwt = JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        when(authFacade.issueAuthTokens(any(UserDetails.class))).thenReturn(jwt);

        ResponseDto<String> expectedResponseDto = ResponseDto.success("login success", HttpStatus.OK.value());
        when(httpUtils.createSuccessResponse(anyString(), anyInt())).thenReturn(expectedResponseDto);

        Cookie expectedATCookie = new Cookie("access_token", accessToken);
        Cookie expectedRTCookie = new Cookie("refresh_token", refreshToken);
        when(httpUtils.getCookieOfAccessToken(accessToken)).thenReturn(expectedATCookie);
        when(httpUtils.getCookieOfRefreshToken(refreshToken)).thenReturn(expectedRTCookie);

        Authentication authentication = createAuthentication(newUser, dummyPassword);
        loginSuccessHandler.onAuthenticationSuccess(null, response, authentication);

        verify(authFacade, times(1)).issueAuthTokens(any(UserDetails.class));
        verify(httpUtils, times(1)).createSuccessResponse(anyString(), anyInt());
        verify(httpUtils, times(1)).getCookieOfAccessToken(anyString());
        verify(httpUtils, times(1)).getCookieOfRefreshToken(anyString());
        verify(httpUtils, times(1)).setResponseWithCookies(
                any(HttpServletResponse.class), eq(expectedResponseDto), eq(expectedATCookie), eq(expectedRTCookie)
        );
    }
}
