package com.essence.essencebackend.music.album.repository;

import com.essence.essencebackend.music.album.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findTop20ByOrderByTotalStreamsDesc();

    @Query("SELECT DISTINCT a FROM Album a LEFT JOIN FETCH a.albumArtists aa LEFT JOIN FETCH aa.artist ORDER BY a.totalStreams DESC LIMIT 20")
    List<Album> findTop20WithArtistsByTotalStreams();

    Optional<Album> findByAlbumUrl(String albumUrl);
}
