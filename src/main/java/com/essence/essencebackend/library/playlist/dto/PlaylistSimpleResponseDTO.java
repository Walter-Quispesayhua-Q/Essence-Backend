package com.essence.essencebackend.library.playlist.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record PlaylistSimpleResponseDTO(
        Long id,
        String title,
        Boolean isPublic,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long totalLikes
) {
}
