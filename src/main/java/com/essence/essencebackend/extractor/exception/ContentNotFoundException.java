package com.essence.essencebackend.extractor.exception;

public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException(String identifier) {
        super("Contenido no encontrado: " + identifier);
    }
}
