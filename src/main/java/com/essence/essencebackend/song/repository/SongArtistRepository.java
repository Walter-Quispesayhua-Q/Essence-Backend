package com.essence.essencebackend.song.repository;

import com.essence.essencebackend.shared.model.embedded.SongArtistId;
import com.essence.essencebackend.song.model.SongArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongArtistRepository extends JpaRepository<SongArtist, SongArtistId> {
}
