package com.essence.essencebackend.music.song.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record SongRequestDTO(

        @NotNull(message = "el titulo no puede estar vacío")
        String title,

        @NotNull(message = "no puede estar vació")
        Integer durationMs,

        LocalDate releaseDate,
        char isrc,

        List<Long> artistIds,
        Long albumId
) {
}
