package com.essence.essencebackend.library.playlist.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.playlist.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    boolean existsByTitleAndUser(String title, User user);

    Optional<Playlist> findByPlaylistIdAndUser(Long playlistId, User user);
}
