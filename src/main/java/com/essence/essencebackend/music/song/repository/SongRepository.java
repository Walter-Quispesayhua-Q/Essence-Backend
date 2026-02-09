package com.essence.essencebackend.music.song.repository;

import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findTop20ByOrderByTotalStreamsDesc();

    Optional<Song> findByHlsMasterKey(String hlsMasterKey);

    @Query("""
        SELECT s FROM Song s
        LEFT JOIN FETCH s.album
        LEFT JOIN FETCH s.songArtists sa
        LEFT JOIN FETCH sa.artist
        WHERE s.hlsMasterKey = :hlsMasterKey
        """)
    Optional<Song> findByHlsMasterKeyWithRelations(@Param("hlsMasterKey") String hlsMasterKey);

    @Query("SELECT s FROM Song s LEFT JOIN FETCH s.songArtists sa LEFT JOIN FETCH sa.artist WHERE s.id IN :ids")
    List<Song> findAllByIdWithArtists(@Param("ids") List<Long> ids);
}
