package com.koliving.api.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "server")
public class ServerProperties {

    @NotBlank
    private final String defaultLocale;

    @NotBlank
    private final String currentVersion;

    @NotBlank
    private final String origin;

    public ServerProperties(String defaultLocale, String currentVersion, String origin) {
        this.defaultLocale = defaultLocale;
        this.currentVersion = currentVersion;
        this.origin = origin;
    }
}
