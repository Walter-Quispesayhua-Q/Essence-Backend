package com.essence.essencebackend.music.song.service;

import com.essence.essencebackend.music.shared.dto.IdStreamingRequestDTO;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;

public interface SongService {
    SongResponseDTO getSongId(IdStreamingRequestDTO data, String username);
}
