package com.essence.essencebackend.extractor.dto;

public record ExtractedArtistDTO(
        String nameArtist,
        String imageKey,
        String description,
        String country
) {
}
