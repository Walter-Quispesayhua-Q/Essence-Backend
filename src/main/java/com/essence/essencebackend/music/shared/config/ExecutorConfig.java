package com.essence.essencebackend.music.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean(name = "songBatchExecutor", destroyMethod = "shutdown")
    public ExecutorService songBatchExecutor() {
        return Executors.newFixedThreadPool(5);
    }
}
