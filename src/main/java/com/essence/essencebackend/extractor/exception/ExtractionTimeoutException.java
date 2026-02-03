package com.essence.essencebackend.extractor.exception;

public class ExtractionTimeoutException extends RuntimeException {
    public ExtractionTimeoutException(String url) {
        super("timeout al extraer datos de: " + url);
    }
}
