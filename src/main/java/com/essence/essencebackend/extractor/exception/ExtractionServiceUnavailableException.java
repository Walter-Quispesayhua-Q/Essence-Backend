package com.essence.essencebackend.extractor.exception;

public class ExtractionServiceUnavailableException extends RuntimeException {
    public ExtractionServiceUnavailableException() {
        super("El servicio de extracción no está disponible");
    }
}
