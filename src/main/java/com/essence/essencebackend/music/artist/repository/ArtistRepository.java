package com.essence.essencebackend.music.artist.repository;

import com.essence.essencebackend.music.artist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findTop20ByOrderByTotalStreamsDesc();

    Optional<Artist> findByArtistUrl(String artistUrl);

//    obtener nombre de artista normalizado.
      @Query("SELECT a FROM Artist a WHERE a.nameNormalized = :name")
      Optional<Artist> findByNameNormalized(@Param("name") String name);
}
