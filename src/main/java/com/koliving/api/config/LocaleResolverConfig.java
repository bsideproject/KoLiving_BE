package com.koliving.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
public class LocaleResolverConfig {

    @Bean
    public LocaleResolver localeResolver() {
        return new CustomLocaleResolver();
    }
}
