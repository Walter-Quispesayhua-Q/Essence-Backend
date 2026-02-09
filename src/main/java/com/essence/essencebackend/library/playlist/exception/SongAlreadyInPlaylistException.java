package com.essence.essencebackend.library.playlist.exception;

public class SongAlreadyInPlaylistException extends RuntimeException {
    public SongAlreadyInPlaylistException(Long songId, Long playlistId) {
        super("la canción con el UrlId: " + songId + " ya existe en la playlist con el UrlId: " + playlistId);
    }
}
