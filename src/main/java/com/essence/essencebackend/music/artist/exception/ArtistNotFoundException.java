package com.essence.essencebackend.music.artist.exception;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(Long artistId) {
        super("Artista no encontrado con UrlId: " + artistId);
    }
}