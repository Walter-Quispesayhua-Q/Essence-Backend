package com.essence.essencebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class EssenceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EssenceBackendApplication.class, args);
    }

}
