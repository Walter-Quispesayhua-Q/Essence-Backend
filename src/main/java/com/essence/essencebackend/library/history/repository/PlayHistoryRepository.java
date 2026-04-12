package com.essence.essencebackend.library.history.repository;

import com.essence.essencebackend.library.history.model.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;


public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {

    // Paso 1: obtener IDs paginados eficientemente
    @Query("""
    SELECT ph.id FROM PlayHistory ph 
    WHERE ph.user.id = :userId 
    AND ph.playedAt = (
        SELECT MAX(ph2.playedAt) 
        FROM PlayHistory ph2 
        WHERE ph2.user.id = :userId 
        AND ph2.song.id = ph.song.id
    )
    ORDER BY ph.playedAt DESC
    """)
    List<Long> findRecentUniqueIds(@Param("userId") Long userId, Pageable pageable);

    // Paso 2: cargar entidades completas con relaciones
    @Query("""
    SELECT ph FROM PlayHistory ph 
    JOIN FETCH ph.song s 
    LEFT JOIN FETCH s.album 
    LEFT JOIN FETCH s.songArtists sa 
    LEFT JOIN FETCH sa.artist 
    WHERE ph.id IN :ids 
    ORDER BY ph.playedAt DESC
    """)
    List<PlayHistory> findByIdsWithRelations(@Param("ids") List<Long> ids);
}
