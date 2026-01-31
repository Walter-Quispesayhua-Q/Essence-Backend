package com.essence.essencebackend.music.song.dto;

import java.time.LocalDate;

public record SongResponseSimpleDTO(
        Long id,
        String title,
        Integer durationMs,
        String hlsMasterKey,
        String imageKey,
        String songType,
        Long totalPlays,
        String artistName,
        String albumName,
        LocalDate releaseDate
) {
}
