package com.essence.essencebackend.music.song.dto;

public record SongResponseSimpleDTO(
        Long id,
        String title,
        Integer durationMs,
        String hlsMasterKey,
        String imageKey,
        String songType,
        Long totalPlays
) {
}
