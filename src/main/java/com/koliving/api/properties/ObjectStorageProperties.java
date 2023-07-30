package com.koliving.api.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "cloud.aws")
public class ObjectStorageProperties {

    private final Credentials credentials;
    private final Region region;
    private final S3 s3;

    public ObjectStorageProperties(Credentials credentials, Region region, S3 s3) {
        this.credentials = credentials;
        this.region = region;
        this.s3 = s3;
    }

    @Getter
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
    public static class Region {

        @NotBlank
        private final String static1;

        public Region(String static1) {
            this.static1 = static1;
        }
    }

    @Getter
    public static class S3 {

        @NotBlank
        private final String bucket;

        public S3(String bucket) {
            this.bucket = bucket;
        }
    }
}
