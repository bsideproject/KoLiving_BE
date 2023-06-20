package com.koliving.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    private String currentVersion;

    public SecurityConfig(@Value("${server.current-version:v1}") String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
