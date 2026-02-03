package com.essence.essencebackend.music.artist.repository;

import com.essence.essencebackend.music.artist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findTop20ByOrderByTotalStreamsDesc();
}
