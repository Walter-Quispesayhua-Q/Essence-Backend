package com.essence.essencebackend.music.song.repository;

import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findTop20ByOrderByTotalStreamsDesc();

    Optional<Song> findByHlsMasterKey(String hlsMasterKey);

    @Query("""
        SELECT DISTINCT s FROM Song s
        LEFT JOIN FETCH s.album
        LEFT JOIN FETCH s.songArtists sa
        LEFT JOIN FETCH sa.artist
        WHERE s.hlsMasterKey = :hlsMasterKey
        """)
    Optional<Song> findByHlsMasterKeyWithRelations(@Param("hlsMasterKey") String hlsMasterKey);

    @Query("SELECT s FROM Song s LEFT JOIN FETCH s.songArtists sa LEFT JOIN FETCH sa.artist WHERE s.id IN :ids")
    List<Song> findAllByIdWithArtists(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.streamingUrl = :streamingUrl, s.lastSyncedAt = :syncedAt WHERE s.hlsMasterKey = :videoId")
    int updateStreamingUrl(@Param("videoId") String videoId,
                           @Param("streamingUrl") String streamingUrl,
                           @Param("syncedAt") Instant syncedAt);
}
