package com.koliving.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api(@Value("${springdoc.version}") String springdocVersion) {
        Info info = new Info()
                .title("Koliving openApi")
                .version(springdocVersion)
                .description("Koliving");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
