package com.essence.essencebackend.home.dto;

public record HomeStatusDTO(
        boolean songsLoaded,
        boolean albumsLoaded,
        boolean artistsLoaded,
        String error
) {
}
