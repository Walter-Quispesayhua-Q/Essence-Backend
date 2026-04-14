package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.dto.InvidiousStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Component
public class InvidiousStreamExtractor {

    /** Extraer mejor URL de audio */
    public Optional<String> extractBestAudioUrl(InvidiousStreamResponse response) {
        if (response.getAdaptiveFormats() == null || response.getAdaptiveFormats().isEmpty()) {
            log.warn("Invidious: no hay adaptiveFormats disponibles");
            return Optional.empty();
        }

        return response.getAdaptiveFormats().stream()
                .filter(f -> f.getType() != null && f.getType().startsWith("audio/"))
                .max(Comparator.comparingInt(f -> parseBitrate(f.getBitrate())))
                .map(format -> buildProxyUrl(response.getVideoId(), format.getItag()))
                .filter(url -> url != null && !url.isBlank());
    }

    /** Construir URL proxy de Invidious */
    private String buildProxyUrl(String videoId, String itag) {
        return "https://inv.thepixora.com/latest_version?id=" + videoId
                + "&itag=" + itag + "&local=true";
    }

    // METADATA

    public String extractTitle(InvidiousStreamResponse response) {
        return response.getTitle();
    }

    public int extractDurationMs(InvidiousStreamResponse response) {
        return response.getLengthSeconds() * 1000;
    }

    public String extractThumbnailUrl(InvidiousStreamResponse response) {
        if (response.getVideoThumbnails() == null || response.getVideoThumbnails().isEmpty()) {
            return null;
        }
        return response.getVideoThumbnails().stream()
                .max(Comparator.comparingInt(InvidiousStreamResponse.VideoThumbnail::getHeight))
                .map(InvidiousStreamResponse.VideoThumbnail::getUrl)
                .orElse(null);
    }

    public long extractViewCount(InvidiousStreamResponse response) {
        return Math.max(response.getViewCount(), 0);
    }

    public LocalDate extractUploadDate(InvidiousStreamResponse response) {
        if (response.getPublished() <= 0) {
            return null;
        }
        try {
            return Instant.ofEpochSecond(response.getPublished())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();
        } catch (Exception e) {
            log.warn("Invidious: no se pudo parsear fecha published: {}", response.getPublished());
            return null;
        }
    }

    public String extractUploaderName(InvidiousStreamResponse response) {
        return response.getAuthor();
    }

    public String extractUploaderUrl(InvidiousStreamResponse response) {
        String authorId = response.getAuthorId();
        if (authorId == null || authorId.isBlank()) {
            return null;
        }
        return "https://music.youtube.com/channel/" + authorId;
    }

    private int parseBitrate(String bitrate) {
        if (bitrate == null || bitrate.isBlank()) return 0;
        try {
            return Integer.parseInt(bitrate.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}