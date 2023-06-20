package com.koliving.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

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
}
