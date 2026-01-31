package com.essence.essencebackend.music.song.service;

import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public interface SongService {
    List<SongResponseSimpleDTO> getTrendingSongs(String username);
}
