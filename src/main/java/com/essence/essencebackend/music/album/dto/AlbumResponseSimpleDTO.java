package com.essence.essencebackend.music.album.dto;

import java.time.LocalDate;

public record AlbumResponseSimpleDTO(
        Long id,
        String title,
        String imageKey,
        LocalDate releaseDate
) {
}
