package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.dto.PipedStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;


@Slf4j
@Component
public class PipedStreamExtractor {

    public Optional<String> extractBestAudioUrl(PipedStreamResponse response) {
        if (response.getAudioStreams() == null || response.getAudioStreams().isEmpty()) {
            log.warn("Piped: no hay audioStreams disponibles");
            return Optional.empty();
        }

        return response.getAudioStreams().stream()
                .filter(s -> !s.isVideoOnly())
                .max(Comparator.comparingInt(PipedStreamResponse.AudioStream::getBitrate))
                .map(PipedStreamResponse.AudioStream::getUrl)
                .filter(url -> !url.isBlank());
    }

    public String extractTitle(PipedStreamResponse response) {
        return response.getTitle();
    }

    public int extractDurationMs(PipedStreamResponse response) {
        return response.getDuration() * 1000;
    }

    public String extractThumbnailUrl(PipedStreamResponse response) {
        return response.getThumbnailUrl();
    }

    public long extractViewCount(PipedStreamResponse response) {
        return Math.max(response.getViews(), 0);
    }

    public LocalDate extractUploadDate(PipedStreamResponse response) {
        String date = response.getUploadDate();
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(date.substring(0, 10));
        } catch (Exception e) {
            log.warn("Piped: no se pudo parsear fecha '{}': {}", date, e.getMessage());
            return null;
        }
    }

    public String extractUploaderName(PipedStreamResponse response) {
        return response.getUploader();
    }

    public String extractChannelId(PipedStreamResponse response) {
        String url = response.getUploaderUrl();
        if (url == null || url.isBlank()) {
            return null;
        }
        if (url.contains("/channel/")) {
            return url.substring(url.lastIndexOf("/channel/") + 9);
        }
        return url;
    }

    public String extractUploaderUrl(PipedStreamResponse response) {
        String channelId = extractChannelId(response);
        if (channelId == null) {
            return null;
        }
        return "https://music.youtube.com/channel/" + channelId;
    }
}