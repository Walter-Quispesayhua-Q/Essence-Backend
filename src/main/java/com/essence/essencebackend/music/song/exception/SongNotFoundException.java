package com.essence.essencebackend.music.song.exception;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(String identifier) {
        super("canción no encontrada: " + identifier);
    }

    public SongNotFoundException(Long songId) {
        this(String.valueOf(songId));
    }
}
