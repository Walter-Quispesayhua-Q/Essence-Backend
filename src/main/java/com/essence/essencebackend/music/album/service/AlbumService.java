package com.essence.essencebackend.music.album.service;

import com.essence.essencebackend.music.album.dto.AlbumResponseDTO;

public interface AlbumService {
    AlbumResponseDTO getAlbumDetail(String username, String albumUrlOrId);
}
