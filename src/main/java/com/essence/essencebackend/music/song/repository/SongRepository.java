package com.essence.essencebackend.music.song.repository;

import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findTop20ByOrderByTotalStreamsDesc();
}
