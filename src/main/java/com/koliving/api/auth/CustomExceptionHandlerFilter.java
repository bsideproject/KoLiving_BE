package com.koliving.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koliving.api.dto.JwtTokenExceptionDto;
import com.koliving.api.dto.ResponseDto;
import com.koliving.api.dto.ValidationResult;
import com.koliving.api.exception.BlackListTokenException;
import com.koliving.api.exception.LoginInvalidException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

@RequiredArgsConstructor
public class CustomExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Locale locale = localeResolver.resolveLocale(request);

        try {
            filterChain.doFilter(request, response);
        } catch (LoginInvalidException e) {
            ValidationResult errors = ValidationResult.of(e.getErrors(), messageSource, locale);

            setErrorResponse(response,
                    ResponseDto.failure(errors, HttpServletResponse.SC_BAD_REQUEST));
        } catch (AuthenticationException | JwtException e) {
            String errorMessage = messageSource.getMessage(e.getMessage(), null, locale);

            setRedirectResponse(
                    response,
                    ResponseDto.failure(new JwtTokenExceptionDto(errorMessage), HttpServletResponse.SC_UNAUTHORIZED),
                    getRedirectUri(request, "/login")
            );
        } catch (BlackListTokenException e) {
            setErrorResponse(response,
                    ResponseDto.failure(e.getMessage(), HttpServletResponse.SC_FORBIDDEN));
        } catch (RuntimeException e) {
            setErrorResponse(response,
                    ResponseDto.failure("mapping error", HttpServletResponse.SC_BAD_REQUEST));
        }
    }

    private String getRedirectUri(HttpServletRequest request, String redirectPath) {
        URI currentUri = URI.create(request.getRequestURI());

        return UriComponentsBuilder.fromUri(currentUri)
                .path(redirectPath)
                .build().toUri().toString();
    }

    private void setErrorResponse(HttpServletResponse response, ResponseDto body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(body.getResponseCode());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private void setRedirectResponse(HttpServletResponse response, ResponseDto body, String redirectUri) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(body.getResponseCode());
        response.setHeader("Location", redirectUri);

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
