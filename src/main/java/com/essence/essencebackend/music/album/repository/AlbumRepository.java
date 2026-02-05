package com.essence.essencebackend.music.album.repository;

import com.essence.essencebackend.music.album.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findTop20ByOrderByTotalStreamsDesc();

    Optional<Album> findByAlbumUrl(String albumUrl);
}
