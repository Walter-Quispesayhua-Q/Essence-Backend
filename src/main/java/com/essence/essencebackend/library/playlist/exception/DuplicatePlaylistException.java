package com.essence.essencebackend.library.playlist.exception;

public class DuplicatePlaylistException extends RuntimeException{
    public DuplicatePlaylistException(String message) {
        super("Ya tienes una playlist con ese nombre" + message);
    }
}
