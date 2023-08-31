package com.koliving.api.auth.logout;

import com.koliving.api.auth.AuthFacade;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final AuthFacade authFacade;
    private final HttpUtils httpUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String accessToken = httpUtils.resolveToken(request);
        authFacade.addToBlackList(accessToken);

        SecurityContextHolder.clearContext();

        httpUtils.setResponseWithRedirect(
                response,
                ResponseDto.success("logout success", HttpServletResponse.SC_ACCEPTED),
                httpUtils.getCurrentVersionPath("home")
        );
    }
}
