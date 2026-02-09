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

//    usando youtube
    private static final int YOUTUBE = 0;
    private StreamingService streamingService;


    @PostConstruct
    public void init() {
        try {
            NewPipe.init(new CustomDownloader());
            streamingService = NewPipe.getService(YOUTUBE);
            log.info("NewPipe inicializado: {}", streamingService.getServiceInfo().getName());
        } catch (Exception e) {
            log.warn("NewPipe no disponible: {}", e.getMessage());
            streamingService = null;
        }
    }
    @Bean
    public StreamingService streamingService() {
        return streamingService;
    }
}
