package com.essence.essencebackend.music.artist.repository;

import com.essence.essencebackend.music.artist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
