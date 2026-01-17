package com.essence.essencebackend.album.repository;

import com.essence.essencebackend.album.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
