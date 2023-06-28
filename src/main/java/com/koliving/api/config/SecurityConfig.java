package com.koliving.api.config;

import com.koliving.api.filter.JwtAuthenticationFilter;
import com.koliving.api.provider.JwtProvider;
import com.koliving.api.token.IJwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userService;
    private final JwtProvider jwtProvider;
    private final IJwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private String currentVersion;

    public SecurityConfig(UserDetailsService userService,
                          JwtProvider jwtProvider,
                          IJwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          @Value("${server.current-version:v1}") String currentVersion) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.currentVersion = currentVersion;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) ->
                web.ignoring().requestMatchers(
                        "/resources/**",
                        "/swagger-ui/**"
                );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, jwtService);
        String rootPath = String.format("/api/%s", currentVersion);

        http
            .formLogin().disable()
            .httpBasic().disable();

        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
            .addFilter(corsFilter())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
            .authorizeRequests()
                // TODO : Add more specific path (No need to authenticate)
                .requestMatchers(rootPath + "/signup/**").permitAll()
            .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        String rootPath = String.format("/api/%s", currentVersion);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration(rootPath + "/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }
}
