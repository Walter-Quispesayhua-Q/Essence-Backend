package com.essence.essencebackend.extractor.config;

import com.essence.essencebackend.extractor.exception.ExternalRequestTimeoutException;
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
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomDownloader extends Downloader {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT  = Duration.ofSeconds(15);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public Response execute(@NonNull Request request) throws IOException, ReCaptchaException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(request.url()))
                    .timeout(REQUEST_TIMEOUT);

            Map<String, List<String>> headers = new LinkedHashMap<>();

            if (request.localization() != null) {
                headers.putAll(Request.getHeadersFromLocalization(request.localization()));
            }

            if (request.headers() != null) {
                headers.putAll(request.headers());
            }

            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                for (String value : entry.getValue()) {
                    builder.header(entry.getKey(), value);
                }
            }

            String method = request.httpMethod() != null
                    ? request.httpMethod().toUpperCase()
                    : "GET";
            byte[] body = request.dataToSend();

            switch (method) {
                case "POST" -> builder.POST(body != null
                        ? HttpRequest.BodyPublishers.ofByteArray(body)
                        : HttpRequest.BodyPublishers.noBody());
                case "HEAD" -> builder.method("HEAD", HttpRequest.BodyPublishers.noBody());
                case "GET"  -> builder.GET();
                default     -> builder.method(method, body != null
                        ? HttpRequest.BodyPublishers.ofByteArray(body)
                        : HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            return new Response(
                    response.statusCode(),
                    null,
                    response.headers().map(),
                    response.body(),
                    request.url()
            );
        } catch (java.net.http.HttpTimeoutException e) {
            throw new ExternalRequestTimeoutException(request.url(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrumpido", e);
        }
    }
}
