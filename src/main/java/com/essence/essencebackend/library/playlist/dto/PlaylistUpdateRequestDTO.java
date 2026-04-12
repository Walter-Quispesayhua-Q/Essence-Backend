package com.essence.essencebackend.library.playlist.dto;

import jakarta.validation.constraints.Size;

public record PlaylistUpdateRequestDTO(

        @Size(max = 255, message = "El titulo no puede superar los 255 caracteres")
        String title,

        String description,
        Boolean isPublic
) {
}
