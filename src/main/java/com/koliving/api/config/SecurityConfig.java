package com.koliving.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userService;
    private final PasswordEncoder passwordEncoder;
    private String currentVersion;

    public SecurityConfig(UserDetailsService userService,
            PasswordEncoder passwordEncoder,
            @Value("${server.current-version:v1}") String currentVersion) {
        this.userService = userService;
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
        String rootPath = String.format("/api/%s", currentVersion);

        http.authorizeRequests()
                // TODO : Add more specific path (No need to authenticate)
                .requestMatchers(rootPath + "/signup/**").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }


}
