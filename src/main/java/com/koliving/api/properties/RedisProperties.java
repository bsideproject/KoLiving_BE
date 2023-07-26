package com.koliving.api.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    @NotEmpty
    private final String host;

    @NotNull
    @Positive
    private final int port;

    public RedisProperties(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
