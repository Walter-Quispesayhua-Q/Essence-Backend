package com.essence.essencebackend.music.album.exception;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(String identifier) {
        super("Album no encontrado: " + identifier);
    }

    public AlbumNotFoundException(Long albumId) {
        this(String.valueOf(albumId));
    }
}
