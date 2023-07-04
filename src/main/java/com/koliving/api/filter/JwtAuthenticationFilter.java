package com.koliving.api.filter;

import com.koliving.api.provider.JwtProvider;
import com.koliving.api.token.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = null;

        try {
            accessToken = resolveToken(request);
            jwtProvider.validateToken(accessToken);
        } catch (RuntimeException e) {
            setResponse(response, e.getMessage());
            return;
        }

        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            throw new RuntimeException("Authorization header not found");
        }

        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw new RuntimeException("Authorization header does not start with Bearer");
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
