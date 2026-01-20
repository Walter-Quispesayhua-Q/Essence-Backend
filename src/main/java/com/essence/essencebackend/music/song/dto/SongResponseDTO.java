package com.essence.essencebackend.music.song.dto;


import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;

import java.time.LocalDate;
import java.util.List;

public record SongResponseDTO(
        Long id,
        String title,
        Integer durationMs,
        LocalDate releaseDate,
        String hlsMasterKey,
        String imageKey,
        String songType,
        Long totalPlays,

        List<ArtistResponseSimpleDTO> artists,
        AlbumResponseSimpleDTO album
) {
}
