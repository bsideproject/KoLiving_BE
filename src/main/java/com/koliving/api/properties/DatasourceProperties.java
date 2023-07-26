package com.koliving.api.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceProperties {

    @NotEmpty
    private final String url;

    @NotEmpty
    private final String username;

    @NotEmpty
    private final String password;

    @NotEmpty
    private final String driverClassName;

    public DatasourceProperties(String url, String username, String password, String driverClassName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }
}
