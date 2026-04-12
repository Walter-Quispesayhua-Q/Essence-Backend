package com.essence.essencebackend.library.playlist.exception;

public class SystemPlaylistCannotBeModifiedException extends RuntimeException {
    public SystemPlaylistCannotBeModifiedException() {
        super("Las playlists del sistema no pueden ser modificadas");
    }
}