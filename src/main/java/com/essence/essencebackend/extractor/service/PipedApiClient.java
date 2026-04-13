package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.config.PipedProperties;
import com.essence.essencebackend.extractor.dto.PipedStreamResponse;
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

/**
 * Cliente HTTP resiliente para comunicarse con instancias públicas de Piped.
 * Implementa retry automático entre múltiples instancias:
 * si una instancia falla, intenta la siguiente automáticamente.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PipedApiClient {

    private final PipedProperties pipedProperties;
    private final ObjectMapper objectMapper;
    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        List<String> instances = pipedProperties.getInstances();
        log.info("PipedApiClient inicializado con {} instancia(s): {}",
                instances.size(), instances);
    }

    /**
     * Realiza una petición GET a Piped probando todas las instancias configuradas.
     * Devuelve el body como String si alguna instancia responde con 200.
     */
    public Optional<String> getRaw(String path) {
        if (!pipedProperties.isEnabled()) {
            log.debug("Piped API está deshabilitado por configuración");
            return Optional.empty();
        }

        Duration timeout = Duration.ofSeconds(pipedProperties.getTimeoutSeconds());

        for (String instance : pipedProperties.getInstances()) {
            try {
                String url = instance + path;
                log.info("Piped GET: {}", url);

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
                    log.info("Piped OK desde: {}", instance);
                    return Optional.of(response.body());
                }

                log.warn("Piped {} respondió status {}", instance, response.statusCode());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Piped request interrumpido: {}", instance);
                return Optional.empty();
            } catch (Exception e) {
                log.warn("Piped {} falló: {}", instance, e.getMessage());
            }
        }

        log.error("Todas las instancias de Piped fallaron para: {}", path);
        return Optional.empty();
    }

    /**
     * Parsea el body JSON a una clase específica.
     * Genérico: sirve para cualquier tipo de respuesta de Piped.
     */
    public <T> Optional<T> get(String path, Class<T> responseType) {
        return getRaw(path).flatMap(body -> {
            try {
                return Optional.of(objectMapper.readValue(body, responseType));
            } catch (Exception e) {
                log.error("Error parseando respuesta de Piped: {}", e.getMessage());
                return Optional.empty();
            }
        });
    }

    /**
     * Atajo para obtener la metadata de un stream.
     */
    public Optional<PipedStreamResponse> getStreamInfo(String videoId) {
        return get("/streams/" + videoId, PipedStreamResponse.class);
    }
}