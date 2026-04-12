package com.essence.essencebackend.library.playlist.dto;

import java.util.List;

public record PlaylistListResponseDTO(
        List<PlaylistSimpleResponseDTO> myPlaylists,
        List<PlaylistSimpleResponseDTO> playlistsPublic
) {}
