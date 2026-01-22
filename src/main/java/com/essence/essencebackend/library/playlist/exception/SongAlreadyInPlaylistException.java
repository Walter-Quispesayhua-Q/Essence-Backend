package com.essence.essencebackend.library.playlist.exception;

public class SongAlreadyInPlaylistException extends RuntimeException {
    public SongAlreadyInPlaylistException(Long songId, Long playlistId) {
        super("la canción con el id: " + songId + " ya existe en la playlist con el id: " + playlistId);
    }
}
