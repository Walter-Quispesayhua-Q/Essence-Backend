package com.essence.essencebackend.extractor.service.impl;

import com.essence.essencebackend.extractor.config.TrendingKioskProperties;
import com.essence.essencebackend.extractor.exception.ContentNotFoundException;
import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.extractor.exception.ExtractionTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.essence.essencebackend.extractor.exception.ExternalRequestTimeoutException;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@Service
@DependsOn("streamingService")
public class ExtractorTrendingService {

    private final StreamingService streamingService;
    private final TrendingKioskProperties trendingKioskProperties;

    protected <T, I> List<T> getTrending(
            TrendingType type,
            Function<I, T> mapper
    ) {
        if (streamingService == null) {
            log.warn("YouTube Music no disponible");
            throw new ExtractionServiceUnavailableException();
        }
        String kioskId = trendingKioskProperties.getForType(type);
        try {
            KioskExtractor<?> kiosk = streamingService
                    .getKioskList()
                    .getExtractorById(kioskId, null);
            kiosk.fetchPage();
            List<T> result = kiosk.getInitialPage().getItems().stream()
                    .filter(item -> type.getItemClass().isInstance(item))
                    .map(item -> mapper.apply((I) item))
                    .toList();
            if (result.isEmpty()) {
                throw new ContentNotFoundException(kioskId);
            }
            return result;
        } catch (ExternalRequestTimeoutException e) {
            log.debug("Timeout obteniendo trending {}: {}", kioskId, e.getMessage(), e);
            throw new ExtractionTimeoutException(kioskId);
        } catch (Exception e) {
            log.debug("Trending {} no disponible: {}", kioskId, e.getMessage(), e);
            throw new ExtractionServiceUnavailableException();
        }
    }
}
