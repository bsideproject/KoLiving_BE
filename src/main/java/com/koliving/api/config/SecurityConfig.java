package com.koliving.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koliving.api.auth.CustomExceptionHandlerFilter;
import com.koliving.api.auth.jwt.IJwtService;
import com.koliving.api.auth.jwt.JwtAuthenticationFilter;
import com.koliving.api.auth.jwt.JwtProvider;
import com.koliving.api.auth.login.LoginFailureHandler;
import com.koliving.api.auth.login.LoginFilter;
import com.koliving.api.auth.login.LoginProvider;
import com.koliving.api.auth.login.LoginSuccessHandler;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static String[] AUTHENTICATION_WHITELIST = null;
    private static String[] AUTHORIZATION_WHITELIST = null;

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final CustomLocaleResolver localeResolver;
    private final ObjectPostProcessor<Object> objectPostProcessor;
    private final LoginProvider loginProvider;
    private final LocalValidatorFactoryBean validator;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;
    private final SimpleUrlLogoutSuccessHandler logoutSuccessHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Value("${server.current-version}")
    private String apiVersion;

    @PostConstruct
    private void init() {
        AUTHENTICATION_WHITELIST = new String[]{
            "/api/" + apiVersion + "/auth/**",
            "/api/" + apiVersion + "/management/**",
            "/api/" + apiVersion + "/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**"
        };

        AUTHORIZATION_WHITELIST = new String[]{
            "/api/" + apiVersion + "/login",
            "/api/" + apiVersion + "/logout"
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(loginProvider);

        CustomExceptionHandlerFilter customExceptionHandlerFilter = createExceptionHandlerFilter();
        LoginFilter loginFilter = createLoginFilter(authenticationManager);
        JwtAuthenticationFilter jwtAuthenticationFilter = createJwtAuthenticationFilter();

        http.cors().configurationSource(corsConfigurationSource())
            .and()
            .headers()
            .frameOptions().disable()
            .and()
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf().disable()
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> {
                req
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(AUTHENTICATION_WHITELIST).permitAll()
                    .requestMatchers(AUTHORIZATION_WHITELIST).permitAll()
                    .anyRequest().authenticated();
            })
            .logout(config -> {
                config.logoutUrl("/api/v1/logout")
                    .logoutSuccessUrl("/api/v1/login")
                    .logoutSuccessHandler(logoutSuccessHandler);
            }).exceptionHandling(config -> {
                config.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler);
            });
        //TODO filter 순서 조정 필요
//            .addFilterBefore(customExceptionHandlerFilter, HeaderWriterFilter.class)
//            .addFilterAfter(loginFilter, LogoutFilter.class)
//            .addFilterAfter(jwtAuthenticationFilter, loginFilter.getClass());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider... authenticationProviders)
        throws Exception {
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);

        for (AuthenticationProvider provider : authenticationProviders) {
            builder.authenticationProvider(provider);
        }

        return builder.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private CustomExceptionHandlerFilter createExceptionHandlerFilter() {
        return new CustomExceptionHandlerFilter(objectMapper, messageSource, localeResolver);
    }

    private LoginFilter createLoginFilter(AuthenticationManager authenticationManager) {
        LoginFilter loginFilter = new LoginFilter(authenticationManager, objectMapper, validator);
        loginFilter.setFilterProcessesUrl("/api/v1/login");
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        loginFilter.afterPropertiesSet();

        return loginFilter;
    }

    private JwtAuthenticationFilter createJwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, jwtService);
    }
}
