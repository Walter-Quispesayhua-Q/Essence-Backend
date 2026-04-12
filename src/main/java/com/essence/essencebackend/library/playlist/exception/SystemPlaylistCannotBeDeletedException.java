package com.essence.essencebackend.library.playlist.exception;

public class SystemPlaylistCannotBeDeletedException extends RuntimeException {
    public SystemPlaylistCannotBeDeletedException() {
        super("Las playlists del sistema no pueden ser eliminadas");
    }
}