package com.essence.essencebackend.artist.repository;

import com.essence.essencebackend.artist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
