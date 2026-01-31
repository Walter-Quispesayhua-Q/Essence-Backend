package com.essence.essencebackend.extractor.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Slf4j
@Configuration
public class NewPipeConfig {

    private static final int YOUTUBE_MUSIC = 5;
    private StreamingService streamingService;
    private boolean serviceAvailable = false;

    @PostConstruct
    public void init() {
        try {
            NewPipe.init(new CustomDownloader());
            streamingService = NewPipe.getService(YOUTUBE_MUSIC);
            serviceAvailable = true;
            log.info("NewPipe inicializado correctamente");
        } catch (Exception e) {
            log.warn("NewPipe no disponible: {}. La app funcionará sin extracción.", e.getMessage());
            serviceAvailable = false;
        }
    }

    @Bean
    public Optional<StreamingService> streamingService() {
        return Optional.ofNullable(streamingService);
    }

    @Bean
    public boolean isServiceAvailable() {
        return serviceAvailable;
    }
}
