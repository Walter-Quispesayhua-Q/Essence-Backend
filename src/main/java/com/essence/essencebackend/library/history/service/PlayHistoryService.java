package com.essence.essencebackend.library.history.service;

import com.essence.essencebackend.library.history.dto.PlayHistoryRequestDTO;

public interface PlayHistoryService {
    void addSongToHistory(Long id, String username, PlayHistoryRequestDTO data);
}
