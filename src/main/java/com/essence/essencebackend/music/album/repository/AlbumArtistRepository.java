package com.essence.essencebackend.music.album.repository;

import com.essence.essencebackend.music.album.model.AlbumArtist;
import com.essence.essencebackend.music.shared.model.embedded.AlbumArtistId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumArtistRepository extends JpaRepository<AlbumArtist, AlbumArtistId> {
}
