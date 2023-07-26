package com.koliving.api.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "cloud.aws")
public class ObjectStorageProperties {

    private Credentials credentials;
    private Region region;

    @Getter
    @Setter
    public static class Credentials {

        @NotBlank
        private final String accessKey;

        @NotBlank
        private final String secretKey;

        public Credentials(String accessKey, String secretKey) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
        }
    }

    @Getter
    @Setter
    public static class Region {

        @NotBlank
        private String static1;

        public Region(String static1) {
            this.static1 = static1;
        }
    }

}
