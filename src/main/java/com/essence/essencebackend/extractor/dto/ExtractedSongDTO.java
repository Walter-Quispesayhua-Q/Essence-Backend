package com.essence.essencebackend.extractor.dto;


import java.time.LocalDate;

public record ExtractedSongDTO(
        String hlsMasterKey,
        String title,
        Integer durationMs,
        String imageKey,
        LocalDate releaseDate,
        Long totalStreams
) {
}
