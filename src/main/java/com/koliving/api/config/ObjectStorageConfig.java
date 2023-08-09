package com.koliving.api.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.koliving.api.properties.ObjectStorageProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ObjectStorageConfig {

    private final ObjectStorageProperties objectStorageProperties;

    @Bean
    public AmazonS3Client s3Client() {
        String accessKey = objectStorageProperties.getCredentials().getAccessKey();
        String secretKey = objectStorageProperties.getCredentials().getSecretKey();
        String regionName = objectStorageProperties.getRegion().getStatic1();

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                                        .withRegion(regionName)
                                        .build();
    }
}
