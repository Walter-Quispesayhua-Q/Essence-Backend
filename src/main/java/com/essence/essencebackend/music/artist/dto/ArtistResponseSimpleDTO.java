package com.essence.essencebackend.music.artist.dto;

public record ArtistResponseSimpleDTO(
        Long id,
        String nameArtist,
        String imageKey,
        String artistUrl
) {
}
