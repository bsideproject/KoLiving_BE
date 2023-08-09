package com.koliving.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private String version;
    private String title;

    @Bean
    public OpenAPI apiV1() {
        version = "v1";
        title = "Koliving openApi " + version;

        return new OpenAPI()
                .info(getInfo(version, title))
                .components(getComponents())
                .addServersItem(getServer());
    }

    @Bean
    GroupedOpenApi authGroup() {
        version = "v1";

        String authApi = String.format("/api/%s/auth/**", version);

        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch(authApi)
                .build();
    }

    private Components getComponents() {
        return new Components();
    }

    private Info getInfo(String version, String title) {
        return new Info()
                .version(version)
                .title(title)
                .description("Koliving");
    }

    private Server getServer() {
        return new Server()
                .url("/");
    }
}
