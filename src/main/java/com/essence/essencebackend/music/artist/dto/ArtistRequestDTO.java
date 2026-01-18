package com.essence.essencebackend.music.artist.dto;

import jakarta.validation.constraints.NotNull;

public record ArtistRequestDTO(

        @NotNull(message = "el nombre del artista no puede estar vacío")
        String nameArtist,

        String description,
        String country
) {
}
