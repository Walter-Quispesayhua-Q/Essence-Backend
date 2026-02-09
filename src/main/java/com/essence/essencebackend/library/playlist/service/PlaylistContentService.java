package com.essence.essencebackend.library.playlist.service;

import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public interface PlaylistContentService {

    boolean addSongToPlaylist(Long playlistId, Long songId, String username);
    void deleteSongToPlaylist(Long playlistId, Long songId, String username);

    List<SongResponseSimpleDTO> getSongForPlaylist(Long id, String username);
}
