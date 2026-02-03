package com.essence.essencebackend.home.controller;

import com.essence.essencebackend.extractor.service.MetadataExtractorTrendingService;
import com.essence.essencebackend.home.dto.HomeResponseDTO;
import com.essence.essencebackend.home.dto.HomeStatusDTO;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/home")
public class HomeController {

    private final MetadataExtractorTrendingService metadataExtractorTrendingService;

    @GetMapping
    public HomeResponseDTO getHome() {

        List<SongResponseSimpleDTO> songs = List.of();
        List<AlbumResponseSimpleDTO> albums = List.of();
        List<ArtistResponseSimpleDTO> artists = List.of();

        boolean songsOk = true, albumsOk = true, artistsOk = true;

        try {
            songs = metadataExtractorTrendingService.getTrendingSongs();
        } catch (Exception e) {
            songsOk = false;
        }

        try {
            albums = metadataExtractorTrendingService.getTrendingAlbums();
        } catch (Exception e) {
            albumsOk = false;
        }

        try {
            artists = metadataExtractorTrendingService.getTrendingArtists();
        } catch (Exception e) {
            artistsOk = false;
        }

        return new HomeResponseDTO(
                songs, albums, artists,
                new HomeStatusDTO(songsOk, albumsOk, artistsOk, null)
        );
    }
}
