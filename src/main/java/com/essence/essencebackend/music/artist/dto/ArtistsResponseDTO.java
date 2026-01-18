package com.essence.essencebackend.music.artist.dto;


import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public record ArtistsResponseDTO(
        Long id,
        String nameArtist,
        String description,
        String imageKey,
        String country,

        List<AlbumResponseSimpleDTO> albums,
        List<SongResponseSimpleDTO> songs
) {
}
