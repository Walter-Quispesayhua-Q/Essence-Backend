package com.essence.essencebackend.library.playlist.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

public record PlaylistResponseDTO(
        Long id,
        String title,
        String description,
        String imageKey,
        Boolean isPublic,
        LocalDate createdAt,
        LocalDate updatedAt,
        Integer totalSongs,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Integer totalLikes
) {
}
