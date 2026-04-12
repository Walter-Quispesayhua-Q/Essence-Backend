package com.essence.essencebackend.library.playlist.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.model.PlaylistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    boolean existsByTitleAndUser(String title, User user);

    Optional<Playlist> findByPlaylistIdAndUser(Long playlistId, User user);

    @Query("""
            SELECT p FROM Playlist p
            WHERE p.playlistId = :playlistId
            AND (p.user = :user OR p.isPublic = true)
            """)
    Optional<Playlist> findAccessiblePlaylist(Long playlistId, User user);

    List<Playlist> findAllByUser(User user);

    List<Playlist> findAllByIsPublicTrueAndUserNot(User user);

    Optional<Playlist> findByUserAndType(User user, PlaylistType type);
}
