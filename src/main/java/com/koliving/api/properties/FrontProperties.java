package com.koliving.api.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "front")
public class FrontProperties {

    @NotBlank
    private final String origin;

    public FrontProperties(String origin) {
        this.origin = origin;
    }
}
