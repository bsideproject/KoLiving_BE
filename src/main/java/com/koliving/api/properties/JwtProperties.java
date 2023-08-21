package com.koliving.api.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank
    private final String algorithm;

    @NotBlank
    private final String secret;

    @NotNull
    @Positive
    private final long accessValidity;

    @NotNull
    @Positive
    private final long refreshValidity;

    public JwtProperties(String algorithm, String secret, long accessValidity, long refreshValidity) {
        this.algorithm = algorithm;
        this.secret = secret;
        this.accessValidity = accessValidity;
        this.refreshValidity = refreshValidity;
    }
}
