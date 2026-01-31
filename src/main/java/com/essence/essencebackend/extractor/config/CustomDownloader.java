package com.essence.essencebackend.extractor.config;

import org.jspecify.annotations.NonNull;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class CustomDownloader extends Downloader {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public Response execute(@NonNull Request request) throws IOException, ReCaptchaException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(request.url()));
            // Agregar headers del request
            for (Map.Entry<String, List<String>> header : request.headers().entrySet()) {
                for (String value : header.getValue()) {
                    builder.header(header.getKey(), value);
                }
            }
            // Configurar método HTTP (GET o POST)
            if (request.dataToSend() != null) {
                builder.POST(HttpRequest.BodyPublishers.ofByteArray(request.dataToSend()));
            } else {
                builder.GET();
            }
            // Ejecutar request
            HttpResponse<String> response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            // Retornar Response de NewPipe
            return new Response(
                    response.statusCode(),
                    null,
                    response.headers().map(),
                    response.body(),
                    request.url()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }
}
