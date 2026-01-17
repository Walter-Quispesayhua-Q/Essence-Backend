package com.essence.essencebackend.library.playlist.repository;

import com.essence.essencebackend.library.playlist.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
