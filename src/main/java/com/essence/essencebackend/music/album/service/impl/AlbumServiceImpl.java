package com.essence.essencebackend.music.album.service.impl;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.repository.AlbumRepository;
import com.essence.essencebackend.music.album.service.AlbumService;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UrlExtractor urlExtractor;
    private final SearchAlbumService searchAlbumService;

    @Override
    public Album getOrCreateAlbumBySong(String songName, String artistName) {
        log.info("Obteniendo album para la canción: {} y artista: {}", songName, artistName);

        String albumUrl = searchAlbumService.getAlbumUrl(songName, artistName);

        if (albumUrl == null) return null;

        String albumUrlId = urlExtractor.extractId(albumUrl, UrlExtractor.ContentType.ALBUM);

        Optional<Album> albumExist = albumRepository.findByAlbumUrl(albumUrlId);
        if (albumExist.isPresent()) {
            return albumExist.get();
        }

        try {
            PlaylistInfo albumInfo = searchAlbumService.getAlbumInfoByUrl(albumUrl);
            return albumRepository.save(mapToAlbum(albumInfo));

        } catch (Exception e) {
            log.error("Error al guardar nuevo álbum: {}", e.getMessage());
            return null;
        }
    }

    private Album mapToAlbum(PlaylistInfo info) {
        Album album = new Album();
        album.setTitle(info.getName());
        album.setDescription(info.getDescription().getContent());
        album.setImageKey(info.getThumbnails().isEmpty() ? null
                : info.getThumbnails().get(0).getUrl());
        album.setAlbumUrl(
                urlExtractor.extractId(info.getUrl(), UrlExtractor.ContentType.ALBUM)
        );
        return album;
    }
}
