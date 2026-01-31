package com.essence.essencebackend.extractor.dto;


import java.time.LocalDate;

public record ExtractedAlbumDTO(
        String title,
        String description,
        String imageKey,
        LocalDate releaseDate
) {
}
