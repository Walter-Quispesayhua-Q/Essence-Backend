package com.essence.essencebackend.song.repository;

import com.essence.essencebackend.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
}
