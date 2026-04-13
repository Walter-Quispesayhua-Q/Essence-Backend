package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.dto.InvidiousStreamResponse;
import com.essence.essencebackend.extractor.dto.PipedStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Component
public class PipedStreamExtractor {

    // ===================== PIPED =====================

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

    // ===================== INVIDIOUS =====================

    public Optional<String> extractBestAudioUrl(InvidiousStreamResponse response) {
        if (response.getAdaptiveFormats() == null || response.getAdaptiveFormats().isEmpty()) {
            log.warn("Invidious: no hay adaptiveFormats disponibles");
            return Optional.empty();
        }

        return response.getAdaptiveFormats().stream()
                .filter(f -> f.getType() != null && f.getType().startsWith("audio/"))
                .max(Comparator.comparingInt(f -> parseBitrate(f.getBitrate())))
                .map(format -> buildInvidiousProxyUrl(response.getVideoId(), format.getItag()))
                .filter(url -> url != null && !url.isBlank());
    }

    public Optional<String> extractBestAudioUrl(InvidiousStreamResponse response, String instanceUrl) {
        if (response.getAdaptiveFormats() == null || response.getAdaptiveFormats().isEmpty()) {
            log.warn("Invidious: no hay adaptiveFormats disponibles");
            return Optional.empty();
        }

        return response.getAdaptiveFormats().stream()
                .filter(f -> f.getType() != null && f.getType().startsWith("audio/"))
                .max(Comparator.comparingInt(f -> parseBitrate(f.getBitrate())))
                .map(format -> instanceUrl + "/latest_version?id=" + response.getVideoId()
                        + "&itag=" + format.getItag() + "&local=true");
    }

    private String buildInvidiousProxyUrl(String videoId, String itag) {
        return "https://inv.thepixora.com/latest_version?id=" + videoId
                + "&itag=" + itag + "&local=true";
    }

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
        if (bitrate == null || bitrate.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(bitrate.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}