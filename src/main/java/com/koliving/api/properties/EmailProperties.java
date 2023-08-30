package com.koliving.api.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {

    @NotEmpty
    private final String host;

    @NotNull
    @Positive
    private final int port;

    @NotEmpty
    private final String username;

    @NotEmpty
    private final String password;

    @NotNull
    @Positive
    private long authValidityPeriod;

    public EmailProperties(String host, int port, String username, String password, long authValidityPeriod) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.authValidityPeriod = authValidityPeriod;
    }
}
