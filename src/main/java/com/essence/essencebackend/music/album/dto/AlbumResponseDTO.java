package com.essence.essencebackend.music.album.dto;

import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.time.LocalDate;
import java.util.List;

public record AlbumResponseDTO(
        Long id,
        String title,
        String description,
        String imageKey,
        LocalDate releaseDate,

        List<ArtistResponseSimpleDTO> artists,
        List<SongResponseSimpleDTO> songs
) {
}
