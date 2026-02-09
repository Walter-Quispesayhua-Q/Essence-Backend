
package com.essence.essencebackend.search.dto;

import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public record SearchResponseDTO(
        List<SongResponseSimpleDTO> songs,
        List<AlbumResponseSimpleDTO> albums,
        List<ArtistResponseSimpleDTO> artists
) {}
