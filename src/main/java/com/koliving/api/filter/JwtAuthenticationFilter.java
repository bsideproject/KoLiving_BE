package com.koliving.api.filter;

import com.koliving.api.provider.JwtProvider;
import com.koliving.api.token.IJwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;
    private final String currentVersion;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, IJwtService jwtService, @Value("${server.current-version:v1}") String currentVersion) {
        this.jwtProvider = jwtProvider;
        this.jwtService = jwtService;
        this.currentVersion = currentVersion;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String rootPath = String.format("/api/%s", currentVersion);
        String loginPath = rootPath + "/auth/login";
        String refreshTokenPath = rootPath + "/auth/refresh-token";
        String accessToken = null;

        try {
            accessToken = resolveToken(request);
            if (jwtService.isBlackList(accessToken)) {
                SecurityContextHolder.clearContext();
                response.sendRedirect(loginPath);
                return;
            }
            jwtProvider.validateToken(accessToken);
        } catch (AuthenticationException e) {
            setResponse(response, e.getMessage());
            return;
        } catch (JwtException e) {
            response.sendRedirect(refreshTokenPath);
            return;
        }

        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) throws AuthenticationException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            throw new InsufficientAuthenticationException("Authorization header not found");
        }

        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw new InsufficientAuthenticationException("Authorization header does not start with Bearer");
        }

        return bearerToken.substring(BEARER_PREFIX.length());
    }

    private void setResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(errorMessage);
    }
}
