package com.koliving.api.auth.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koliving.api.auth.AuthFacade;
import com.koliving.api.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessDeniedException;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    public static final String BEARER_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper;
    private final AuthFacade authFacade;
    private final String apiVersion;

    public LogoutSuccessHandler(ObjectMapper objectMapper,
                                AuthFacade authFacade,
                                @Value("${server.current-version}") String apiVersion) {
        this.objectMapper = objectMapper;
        this.authFacade = authFacade;
        this.apiVersion = apiVersion;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws AccessDeniedException {
        String accessToken = resolveToken(request);
        authFacade.addToBlackList(accessToken);

        SecurityContextHolder.clearContext();

        setRedirectResponse(response, ResponseDto.success("logout", HttpServletResponse.SC_ACCEPTED), getRedirectUri(request, String.format("/api/%s/home", apiVersion)));
    }

    private String resolveToken(HttpServletRequest request) throws AccessDeniedException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            throw new AccessDeniedException("not_found_auth_header");
        }

        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw new AccessDeniedException("not_bearer_type");
        }

        return bearerToken.substring(BEARER_PREFIX.length());
    }

    private String getRedirectUri(HttpServletRequest request, String redirectPath) {
        URI currentUri = URI.create(request.getRequestURI());

        return UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toUri().toString();
    }

    private void setRedirectResponse(HttpServletResponse response, ResponseDto body, String redirectUri) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(body.getResponseCode());
        response.setHeader("Location", redirectUri);

        try {
            response.getWriter().write(objectMapper.writeValueAsString(body));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
