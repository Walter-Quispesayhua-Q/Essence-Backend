package com.essence.essencebackend.library.playlist.service;

import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;

public interface PlaylistService {
    PlaylistSimpleResponseDTO createPlaylist(PlaylistRequestDTO data, String username);
}
