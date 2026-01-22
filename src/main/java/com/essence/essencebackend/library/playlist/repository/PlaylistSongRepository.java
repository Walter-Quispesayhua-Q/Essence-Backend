package com.essence.essencebackend.library.playlist.repository;

import com.essence.essencebackend.library.playlist.model.PlaylistSong;
import com.essence.essencebackend.library.playlist.model.embedded.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    List<PlaylistSong> findByPlaylistPlaylistId(Long playlistPlaylistId);
}
