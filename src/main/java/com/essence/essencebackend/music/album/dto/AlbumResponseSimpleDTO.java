package com.essence.essencebackend.music.album.dto;

import java.time.LocalDate;
import java.util.List;

public record AlbumResponseSimpleDTO(
        Long id,
        String title,
        String imageKey,
        String albumUrl,
        List<String> artists,
        LocalDate releaseDate
) {
}
