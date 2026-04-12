package com.essence.essencebackend.extractor.exception;

import java.io.IOException;

public class ExternalRequestTimeoutException extends IOException {

    public ExternalRequestTimeoutException(String url, Throwable cause) {
        super("El servicio externo no respondio a tiempo: " + url, cause);
    }
}
