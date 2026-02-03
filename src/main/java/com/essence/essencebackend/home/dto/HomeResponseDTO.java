package com.essence.essencebackend.home.dto;

import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public record HomeResponseDTO(
        List<SongResponseSimpleDTO> songs,
        List<AlbumResponseSimpleDTO> albums,
        List<ArtistResponseSimpleDTO> artists,
        HomeStatusDTO status
) {
}
