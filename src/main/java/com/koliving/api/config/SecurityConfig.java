package com.koliving.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private String currentVersion;

    public SecurityConfig(@Value("${server.current-version:v1}") String currentVersion) {
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
}
