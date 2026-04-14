package com.essence.essencebackend.music.song.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SongSyncRequestDTO(

        @NotBlank(message = "videoId es obligatorio")
        String videoId,

        @NotBlank(message = "title es obligatorio")
        String title,

        @NotNull(message = "durationMs es obligatorio")
        Integer durationMs,

        @NotBlank(message = "uploaderName es obligatorio")
        String uploaderName,

        @NotBlank(message = "uploaderUrl es obligatorio")
        String uploaderUrl,

        String thumbnailUrl,
        String streamingUrl,
        Long viewCount,
        LocalDate releaseDate
) {}