package com.essence.essencebackend.library.history.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.history.model.PlayHistory;
import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    PlayHistory findByUserIdAndSongId(Long userId, Long songId);

    Optional<PlayHistory> findByUser(User user);

    // Obtener historial con canciones únicas (la más reciente de cada canción)
    @Query("""
        SELECT ph FROM PlayHistory ph 
        WHERE ph.user.id = :userId 
        AND ph.playedAt = (
            SELECT MAX(ph2.playedAt) 
            FROM PlayHistory ph2 
            WHERE ph2.user.id = :userId 
            AND ph2.song.id = ph.song.id
        )
        ORDER BY ph.playedAt DESC
        """)
    List<PlayHistory> findRecentUniqueSongsByUserId(@Param("userId") Long userId);
}
