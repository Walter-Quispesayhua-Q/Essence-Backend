package com.essence.essencebackend.library.history.exception;

public class AddToHistoryFailedException extends RuntimeException {

    public AddToHistoryFailedException(Long songId, String username, Throwable cause) {
        super("No se pudo agregar la canción " + songId +
                " al historial del usuario " + username, cause);
    }
}
