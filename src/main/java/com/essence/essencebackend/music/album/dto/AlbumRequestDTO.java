package com.essence.essencebackend.music.album.dto;

import com.essence.essencebackend.music.song.dto.SongRequestDTO;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AlbumRequestDTO(

        @NotNull(message = "el titulo del album no puede estar vacío")
        String title,

        String description,
        LocalDate releaseDate,

        @NotNull
        List<Long> artistIds,

        List<SongRequestDTO> songs

) {
}
