package com.essence.essencebackend.album.repository;

import com.essence.essencebackend.album.model.AlbumArtist;
import com.essence.essencebackend.shared.model.embedded.AlbumArtistId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumArtistRepository extends JpaRepository<AlbumArtist, AlbumArtistId> {
}
