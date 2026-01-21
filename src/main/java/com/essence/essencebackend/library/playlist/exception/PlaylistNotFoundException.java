package com.essence.essencebackend.library.playlist.exception;

public class PlaylistNotFoundException extends RuntimeException{
    public PlaylistNotFoundException(Long id) {
        super("Playlist no encontrada con id:" + id);
    }
}
