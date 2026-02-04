package com.essence.essencebackend.music.song.repository;

import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findTop20ByOrderByTotalStreamsDesc();
    Optional<Song> findByExternalId(String id);
}
