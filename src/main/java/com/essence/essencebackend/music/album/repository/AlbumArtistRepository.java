package com.essence.essencebackend.music.album.repository;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.model.AlbumArtist;
import com.essence.essencebackend.music.shared.model.embedded.AlbumArtistId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumArtistRepository extends JpaRepository<AlbumArtist, AlbumArtistId> {
    List<AlbumArtist> findByAlbum(Album album);
}
