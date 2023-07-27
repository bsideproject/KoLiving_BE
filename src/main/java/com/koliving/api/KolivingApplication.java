package com.koliving.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.koliving.api.properties")
public class KolivingApplication {

    public static void main(String[] args) {
        SpringApplication.run(KolivingApplication.class, args);
    }

}
