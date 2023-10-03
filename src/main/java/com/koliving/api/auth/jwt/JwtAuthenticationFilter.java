package com.koliving.api.auth.jwt;

import com.koliving.api.exception.BlackListTokenException;
import com.koliving.api.utils.HttpUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;
    private final HttpUtils httpUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, AuthenticationException, JwtException {
        String accessToken = httpUtils.resolveToken(request);
        jwtProvider.validateToken(accessToken);
        if (jwtService.isBlackList(accessToken)) {
            throw new BlackListTokenException(jwtService.extractEmail(accessToken));
        }

        this.setAuthentication(accessToken);

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtService.createAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
