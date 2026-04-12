package com.essence.essencebackend.library.playlist.dto;

public record PlaylistEditResponseDTO(
        Long id,
        String title,
        String description,
        Boolean isPublic
) {
}

