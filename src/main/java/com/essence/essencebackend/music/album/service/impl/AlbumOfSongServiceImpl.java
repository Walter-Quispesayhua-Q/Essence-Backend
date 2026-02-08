package com.essence.essencebackend.music.album.service.impl;

import com.essence.essencebackend.music.album.mapper.AlbumMapperByInfo;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.model.AlbumArtist;
import com.essence.essencebackend.music.album.repository.AlbumRepository;
import com.essence.essencebackend.music.album.service.AlbumOfSongService;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.shared.model.embedded.AlbumArtistId;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumOfSongServiceImpl implements AlbumOfSongService {

    private final AlbumRepository albumRepository;
    private final UrlExtractor urlExtractor;
    private final SearchAlbumService searchAlbumService;
    private final AlbumMapperByInfo albumMapperByInfo;

    @Override
    public Album getOrCreateAlbumBySong(String songName, String artistName, Set<Artist> artists) {
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
            Album album = albumMapperByInfo.mapToAlbum(albumInfo);
            Album savedAlbum = albumRepository.save(album);

            List<AlbumArtist> albumArtists = new ArrayList<>();
            int order = 0;
            for (Artist artist : artists) {
                AlbumArtist aa = new AlbumArtist();
                aa.setId(new AlbumArtistId(savedAlbum.getId(), artist.getId()));
                aa.setAlbum(savedAlbum);
                aa.setArtist(artist);
                aa.setIsPrimary(order == 0);
                aa.setArtistOrder(order++);
                albumArtists.add(aa);
            }
            savedAlbum.setAlbumArtists(albumArtists);

            return albumRepository.save(savedAlbum);
        } catch (Exception e) {
            log.error("Error al guardar nuevo álbum: {}", e.getMessage());
            return null;
        }
    }
}
