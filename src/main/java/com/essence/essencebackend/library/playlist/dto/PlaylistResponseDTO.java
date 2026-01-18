package com.essence.essencebackend.library.playlist.dto;

import java.time.LocalDate;

public record PlaylistResponseDTO(
        Long id,
        String title,
        String description,
        String coverKey,
        Boolean isPublic,
        LocalDate createdAt,
        LocalDate updatedAt,

        Integer totalSongs,
        Integer totalLikes
) {
}
