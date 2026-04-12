package com.essence.essencebackend.library.playlist.exception;

public class PrivatePlaylistLikeException extends RuntimeException {
    public PrivatePlaylistLikeException(Long playlistId) {
        super("No se puede dar like a una playlist privada con id: " + playlistId);
    }
}
