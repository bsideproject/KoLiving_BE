package com.koliving.api.auth.jwt;

import com.koliving.api.exception.BlackListTokenException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, IJwtService jwtService) {
        this.jwtProvider = jwtProvider;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, AuthenticationException, JwtException {
        String accessToken = resolveToken(request);
        jwtProvider.validateToken(accessToken);
        if (jwtService.isBlackList(accessToken)) {
            throw new BlackListTokenException("black_list_token");
        }

        this.setAuthentication(accessToken);

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) throws AuthenticationException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
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

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtService.createAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
