package com.essence.essencebackend.music.song.exception;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(Long id) {
        super("canción no encontrada con el id: " + id);
    }
}
