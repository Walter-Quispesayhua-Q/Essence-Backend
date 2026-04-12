package com.essence.essencebackend.music.artist.exception;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(String identifier) {
        super("Artista no encontrado: " + identifier);
    }

    public ArtistNotFoundException(Long artistId) {
        this(String.valueOf(artistId));
    }
}