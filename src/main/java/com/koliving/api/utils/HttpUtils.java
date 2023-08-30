package com.koliving.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.properties.FrontProperties;
import com.koliving.api.properties.JwtProperties;
import com.koliving.api.properties.ServerProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@Slf4j
@Component
@RequiredArgsConstructor
public class HttpUtils {

    public static final String BEARER_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final ServerProperties serverProperties;
    private final FrontProperties frontProperties;

    public String getCurrentVersionPath(String path) {
        return String.format("/api/%s/%s", serverProperties.getCurrentVersion(), path);
    }

    public String getCurrentVersionUrl(String path) {
        return serverProperties.getOrigin() + getCurrentVersionPath(path);
    }

    public String getFrontUrl(String path) {
        return frontProperties.getOrigin() + path;
    }

    public String resolveToken(HttpServletRequest request) throws AuthenticationException {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (bearerToken == null) {
            log.error("Authorization header not found");
            throw new InsufficientAuthenticationException("not_found_auth_header");
        }

        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            log.error("Authorization header does not start with Bearer");
            throw new InsufficientAuthenticationException("not_bearer_type");
        }

        return bearerToken.substring(BEARER_PREFIX.length());
    }

    public Cookie getCookieOfAccessToken(String accessTokenValue) {
        Cookie accessToken = new Cookie("access_token", accessTokenValue);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setMaxAge((int) getValidityHour(jwtProperties.getAccessValidity()));

        return accessToken;
    }

    public Cookie getCookieOfRefreshToken(String refreshTokenValue) {
        Cookie refreshToken = new Cookie("refresh_token", refreshTokenValue);
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);
        refreshToken.setMaxAge((int) getValidityDay(jwtProperties.getRefreshValidity()));

        return refreshToken;
    }

    public ResponseCookie getResponseCookieOfAccessToken(String value) {
        return ResponseCookie.from("access_token", value)
                .httpOnly(true)
                .secure(true)
                .maxAge(getValidityHour(jwtProperties.getAccessValidity()))
                .build();
    }

    public ResponseCookie getResponseCookieOfRefreshToken(String value) {
        return ResponseCookie.from("refresh_token", value)
                .httpOnly(true)
                .secure(true)
                .maxAge(getValidityDay(jwtProperties.getRefreshValidity()))
                .build();
    }

    public String getRedirectUri(HttpServletRequest request, String redirectPath) {
        URI currentUri = URI.create(request.getRequestURI());

        return UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toUri().toString();
    }

    public <T> ResponseDto<T> createSuccessResponse(T data, int responseCode) {
        return ResponseDto.success(data, responseCode);
    }

    public <T> ResponseDto<T> createFailureResponse(T error, int responseCode) {
        return ResponseDto.failure(error, responseCode);
    }

    public ResponseEntity createResponseEntity(ResponseDto body) {
        return new ResponseEntity<>(body, null, body.getResponseCode());
    }

    public ResponseEntity createResponseEntityWithCookies(ResponseDto body, ResponseCookie... responseCookies) {
        HttpHeaders headers = new HttpHeaders();

        for (ResponseCookie responseCookie : responseCookies) {
            headers.add(SET_COOKIE, responseCookie.toString());
        }

        return new ResponseEntity<>(body, headers, body.getResponseCode());
    }

    public ResponseEntity createResponseEntityWithRedirect(ResponseDto body, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, redirectUri);

        return new ResponseEntity<>(body, headers, body.getResponseCode());
    }

    public void setResponse(HttpServletResponse response, ResponseDto body) throws IOException {
        response.setContentType(String.valueOf(APPLICATION_JSON));
        response.setCharacterEncoding("UTF-8");
        response.setStatus(body.getResponseCode());

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(body));
        writer.flush();
        writer.close();
    }

    public void setResponseWithCookies(HttpServletResponse response, ResponseDto body, Cookie... tokens) throws IOException {
        for (Cookie token : tokens) {
            response.addCookie(token);
        }

        setResponse(response, body);
    }

    public void setResponseWithRedirect(HttpServletResponse response, ResponseDto body, String redirectPath) throws IOException {
        response.setHeader(LOCATION, redirectPath);
        setResponse(response, body);
    }

    private long getValidityHour(long validityPeriod) {
        return 60 * 60 * validityPeriod;
    }

    private long getValidityDay(long validityPeriod) {
        return 60 * 60 * 24 * validityPeriod;
    }
}
