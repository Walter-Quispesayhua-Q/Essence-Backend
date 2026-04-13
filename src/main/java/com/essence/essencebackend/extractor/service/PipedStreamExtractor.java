package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.dto.PipedStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

/**
 * Extrae y transforma la metadata de PipedStreamResponse
 * a los formatos que necesita la lógica de negocio de Essence.
 */
@Slf4j
@Component
public class PipedStreamExtractor {

    /**
     * Extrae la mejor URL de audio (mayor bitrate).
     */
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

    /**
     * Extrae el título del video.
     */
    public String extractTitle(PipedStreamResponse response) {
        return response.getTitle();
    }

    /**
     * Extrae la duración en milisegundos.
     */
    public int extractDurationMs(PipedStreamResponse response) {
        return response.getDuration() * 1000;
    }

    /**
     * Extrae la URL de la mejor miniatura.
     */
    public String extractThumbnailUrl(PipedStreamResponse response) {
        return response.getThumbnailUrl();
    }

    /**
     * Extrae el total de vistas.
     */
    public long extractViewCount(PipedStreamResponse response) {
        return Math.max(response.getViews(), 0);
    }

    /**
     * Extrae la fecha de subida como LocalDate.
     */
    public LocalDate extractUploadDate(PipedStreamResponse response) {
        String date = response.getUploadDate();
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            // Piped devuelve formato "2021-01-01" o "2021-01-01T00:00:00.000Z"
            return LocalDate.parse(date.substring(0, 10));
        } catch (Exception e) {
            log.warn("Piped: no se pudo parsear fecha '{}': {}", date, e.getMessage());
            return null;
        }
    }

    /**
     * Extrae el nombre del artista/uploader.
     */
    public String extractUploaderName(PipedStreamResponse response) {
        return response.getUploader();
    }

    /**
     * Extrae el channelId del uploaderUrl.
     * Piped devuelve "/channel/UCxxxxxx", extraemos solo "UCxxxxxx".
     */
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

    /**
     * Construye la URL completa del canal de YouTube Music.
     */
    public String extractUploaderUrl(PipedStreamResponse response) {
        String channelId = extractChannelId(response);
        if (channelId == null) {
            return null;
        }
        return "https://music.youtube.com/channel/" + channelId;
    }
}