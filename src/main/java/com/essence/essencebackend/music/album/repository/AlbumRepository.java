package com.essence.essencebackend.music.album.repository;

import com.essence.essencebackend.music.album.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
