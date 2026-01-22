package com.essence.essencebackend.library.playlist.exception;

public class FailedToDeletePlaylistException extends RuntimeException {
    public FailedToDeletePlaylistException(Long id) {
        super("Ocurrió un error en el proceso de eliminar la playlist" + id);
    }
}
