package com.essence.essencebackend.library.playlist.service;

import com.essence.essencebackend.library.playlist.dto.PlaylistCreateRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistEditResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistListResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistUpdateRequestDTO;

public interface PlaylistService {

    PlaylistSimpleResponseDTO createPlaylist(PlaylistCreateRequestDTO data, String username);
    PlaylistSimpleResponseDTO updatePlaylist(Long id, PlaylistUpdateRequestDTO dataUpdate, String username);

    PlaylistResponseDTO getPlaylist(Long id, String username);
    PlaylistEditResponseDTO getForUpdate(Long id, String username);
    PlaylistListResponseDTO getAllPlaylists(String username);

    void deletePlaylist(Long id, String username);
}
