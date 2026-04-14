package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.config.InvidiousProperties;
import com.essence.essencebackend.extractor.dto.InvidiousStreamResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class InvidiousApiClient {

    private final InvidiousProperties invidiousProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        log.info("InvidiousApiClient inicializado con {} instancia(s)",
                invidiousProperties.getInstances().size());
    }

    /** Obtener stream info de Invidious (rota entre instancias) */
    public Optional<InvidiousStreamResponse> getStream(String videoId) {
        if (!invidiousProperties.isEnabled()) {
            return Optional.empty();
        }

        Optional<String> raw = fetchFromInstances(
                invidiousProperties.getInstances(),
                "/api/v1/videos/" + videoId
                        + "?fields=title,videoId,author,authorId,authorUrl,"
                        + "lengthSeconds,viewCount,likeCount,published,"
                        + "videoThumbnails,adaptiveFormats",
                invidiousProperties.getTimeoutSeconds());

        return raw.flatMap(body -> {
            try {
                return Optional.of(objectMapper.readValue(body, InvidiousStreamResponse.class));
            } catch (Exception e) {
                log.error("Error parseando respuesta Invidious: {}", e.getMessage());
                return Optional.empty();
            }
        });
    }

    /** Rota entre instancias hasta obtener respuesta 200 */
    private Optional<String> fetchFromInstances(List<String> instances, String path, int timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);

        for (String instance : instances) {
            try {
                String url = instance + path;
                log.info("Fetching: {}", url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(timeout)
                        .header("Accept", "application/json")
                        .header("User-Agent", "EssenceBackend/1.0")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(
                        request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    log.info("OK desde: {}", instance);
                    return Optional.of(response.body());
                }

                log.warn("{} respondió status {}", instance, response.statusCode());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Request interrumpido: {}", instance);
                return Optional.empty();
            } catch (Exception e) {
                log.warn("{} falló: {}", instance, e.getMessage());
            }
        }

        return Optional.empty();
    }
}