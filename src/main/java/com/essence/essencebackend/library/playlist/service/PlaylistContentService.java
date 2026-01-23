package com.essence.essencebackend.library.playlist.service;

import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public interface PlaylistContentService {

    boolean addSongToPlaylist(Long songId, Long playlistId, String username);
    void deleteSongToPlaylist(Long songId, Long playlistId, String username);

    List<SongResponseSimpleDTO> getSongForPlaylist(Long id, String username);
}
