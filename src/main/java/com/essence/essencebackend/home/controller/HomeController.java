package com.essence.essencebackend.home.controller;

import com.essence.essencebackend.extractor.service.MetadataExtractorTrendingService;
import com.essence.essencebackend.home.dto.HomeResponseDTO;
import com.essence.essencebackend.home.dto.HomeStatusDTO;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/home")
public class HomeController {

    private final MetadataExtractorTrendingService metadataExtractorTrendingService;

    @GetMapping
    public ResponseEntity<HomeResponseDTO> getHome() {
        CompletableFuture<Result<SongResponseSimpleDTO>>   songsF   =
                runAsync(metadataExtractorTrendingService::getTrendingSongs, "songs");
        CompletableFuture<Result<AlbumResponseSimpleDTO>>  albumsF  =
                runAsync(metadataExtractorTrendingService::getTrendingAlbums, "albums");
        CompletableFuture<Result<ArtistResponseSimpleDTO>> artistsF =
                runAsync(metadataExtractorTrendingService::getTrendingArtists, "artists");

        CompletableFuture.allOf(songsF, albumsF, artistsF).join();

        Result<SongResponseSimpleDTO>   songs   = songsF.join();
        Result<AlbumResponseSimpleDTO>  albums  = albumsF.join();
        Result<ArtistResponseSimpleDTO> artists = artistsF.join();

        boolean allFailed = !songs.ok && !albums.ok && !artists.ok;

        String errorMessage = allFailed
                ? "No se pudo cargar el contenido principal. Intente nuevamente."
                : null;

        HomeResponseDTO response = new HomeResponseDTO(
                songs.data, albums.data, artists.data,
                new HomeStatusDTO(songs.ok, albums.ok, artists.ok, errorMessage)
        );

        if (allFailed) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        return ResponseEntity.ok(response);
    }

    private <T> CompletableFuture<Result<T>> runAsync(Supplier<List<T>> supplier, String name) {
        return CompletableFuture.supplyAsync(supplier)
                .handle((data, ex) -> {
                    if (ex != null) {
                        log.warn("Trending {} fallo: {}", name, ex.getMessage());
                        return new Result<T>(List.of(), false);
                    }
                    return new Result<>(data, true);
                });
    }

    private record Result<T>(List<T> data, boolean ok) {}
}
