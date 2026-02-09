package com.essence.essencebackend.music.album.exception;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(Long albumId) {
        super("Album no encontrado con UrlId: " + albumId);
    }
}
