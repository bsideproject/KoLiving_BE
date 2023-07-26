package com.koliving.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    @Bean
    public ResourceBundleMessageSource resourceBundleMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("org/hibernate/validator/ValidationMessages");
        messageSource.addBasenames("org/springframework/security/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
