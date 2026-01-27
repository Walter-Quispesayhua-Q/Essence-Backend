package com.essence.essencebackend.library.history.service;

import com.essence.essencebackend.library.history.dto.PlayHistoryRequestDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public interface PlayHistoryService {
    void addSongToHistory(Long id, String username, PlayHistoryRequestDTO data);

    List<SongResponseSimpleDTO> getSongOfHistory(String username);
}
