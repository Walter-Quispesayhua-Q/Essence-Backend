package com.essence.essencebackend.library.playlist.service;

import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;

public interface PlaylistService {

    //create and update
    PlaylistSimpleResponseDTO createPlaylist(PlaylistRequestDTO data, String username);
    PlaylistSimpleResponseDTO updatePlaylist(Long id, PlaylistRequestDTO dataUpdate, String username);

    //get
    PlaylistResponseDTO getPlaylist(Long id, String username);
    PlaylistSimpleResponseDTO getForUpdate(Long id, String username);

    //delete
    void deletePlaylist(Long id, String username);
}
