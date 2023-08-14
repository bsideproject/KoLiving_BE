package com.koliving.api.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koliving.api.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final LocaleResolver customLocaleResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        Locale locale = customLocaleResolver.resolveLocale(request);
        String failureMessage = null;

        if (exception instanceof AuthenticationException) {
            failureMessage = messageSource.getMessage("login_exception", null, locale);
        }

        setResponseWithLocation(response, createFailureResponse(failureMessage, HttpStatus.UNAUTHORIZED), getRedirectUri(request));
    }

    private String getRedirectUri(HttpServletRequest request) {
        String redirectPath = "/api/v1/login";
        URI currentUri = URI.create(request.getRequestURI());

        return UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toString();
    }

    private <T> ResponseDto<T> createFailureResponse(T error, HttpStatus status) {
        return ResponseDto.failure(error, status.value());
    }

    private void setResponseWithLocation(HttpServletResponse response, ResponseDto body, String redirectPath) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Location", redirectPath);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(body));
        writer.flush();
        writer.close();
    }
}
