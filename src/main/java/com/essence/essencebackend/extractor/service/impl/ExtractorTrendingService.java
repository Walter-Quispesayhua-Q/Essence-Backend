package com.essence.essencebackend.extractor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@Service
public class ExtractorTrendingService {

    private final Optional<StreamingService> streamingService;

    protected <T, I> List<T> getTrending(
            TrendingType type,
            Function<I, T> mapper
    ) {
        if (streamingService.isEmpty()) {
            log.warn("YouTube Music no disponible");
            return List.of();
        }
        try {
            KioskExtractor<?> kiosk = streamingService.get()
                    .getKioskList()
                    .getExtractorById(type.getKioskId(), null);
            kiosk.fetchPage();
            return kiosk.getInitialPage().getItems().stream()
                    .filter(item -> type.getItemClass().isInstance(item))
                    .map(item -> mapper.apply((I) item))
                    .toList();
        } catch (Exception e) {
            log.error("Error obteniendo trending: {}", e.getMessage());
            return List.of();
        }
    }
}
