package com.essence.essencebackend.library.playlist.exception;

public class SongNotInPlaylistException extends RuntimeException {
    public SongNotInPlaylistException(Long songId, Long playlistId) {
        super("La canción con id " + songId + " no se encontró en la playlist " + playlistId);
    }
}
