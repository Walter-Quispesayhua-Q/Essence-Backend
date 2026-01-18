package com.essence.essencebackend.library.playlist.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlaylistRequestDTO(

        @NotNull(message = "El titulo de la playlist no puede estar vacío")
        @Size(max = 255)
        String title,

        String description,
        Boolean isPublic
) {
}
