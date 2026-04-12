package com.essence.essencebackend.music.album.service.impl;


import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.library.like.repository.AlbumLikeRepository;
import com.essence.essencebackend.music.album.dto.AlbumResponseDTO;
import com.essence.essencebackend.music.album.mapper.AlbumMapperByInfo;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.model.AlbumArtist;
import com.essence.essencebackend.music.album.repository.AlbumArtistRepository;
import com.essence.essencebackend.music.album.repository.AlbumRepository;
import com.essence.essencebackend.music.album.service.AlbumService;
import com.essence.essencebackend.music.artist.mapper.ArtistMapper;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.service.ArtistOfSongService;
import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.model.embedded.AlbumArtistId;
import com.essence.essencebackend.music.shared.service.UrlBuilder;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.service.SongBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumServiceImpl implements AlbumService {

    private final UrlBuilder urlBuilder;
    private final UrlExtractor urlExtractor;
    private final Optional<StreamingService> streamingService;
    private final AlbumRepository albumRepository;
    private final AlbumMapperByInfo albumMapperByInfo;
    private final SongBatchService songBatchService;
    private final AlbumArtistRepository albumArtistRepository;
    private final ArtistMapper artistMapper;
    private final SongMapper songMapper;
    private final ArtistOfSongService artistOfSongService;
    private final AlbumLikeRepository albumLikeRepository;


    @Override
    public AlbumResponseDTO getAlbumDetail(String username, String albumUrlOrId) {
        log.info("Obteniendo album por el usuario: {}", username);

        String albumUrl = urlBuilder.resolveUrl(albumUrlOrId, ContentType.ALBUM);

        try {
            PlaylistInfo info = PlaylistInfo.getInfo(streamingService.get(), albumUrl);
            String albumUrlId = urlExtractor.extractId(info.getUrl(), ContentType.ALBUM);
            List<StreamInfoItem> songItems = info.getRelatedItems();

            Optional<Album> albumExist = albumRepository.findByAlbumUrl(albumUrlId);

            Album album;
            if (albumExist.isPresent()) {
                album = albumExist.get();
                log.info("Album existente: {}", album.getTitle());
            } else {
                album = albumMapperByInfo.mapToAlbum(info);
                album = albumRepository.save(album);

                if (info.getUploaderUrl() != null && !info.getUploaderUrl().isBlank()) {
                    Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(
                            info.getUploaderUrl(),
                            info.getUploaderName()
                    );
                    if (artists != null && !artists.isEmpty()) {
                        saveAlbumArtists(album, artists);
                    }
                }
                log.info("Album creado: {}", album.getTitle());
            }

            List<Song> songs = songBatchService.saveSongsFromAlbum(album, songItems);
            return buildAlbumResponse(album, songs,username);

        } catch (Exception e) {
            log.error("Error obteniendo album: {}", e.getMessage(), e);
            throw new ExtractionServiceUnavailableException();
        }
    }

    private void saveAlbumArtists(Album album, Set<Artist> artists) {
        List<AlbumArtist> albumArtists = new ArrayList<>();
        int order = 0;
        for (Artist artist : artists) {
            AlbumArtist aa = new AlbumArtist();
            aa.setId(new AlbumArtistId(album.getId(), artist.getId()));
            aa.setAlbum(album);
            aa.setArtist(artist);
            aa.setIsPrimary(order == 0);
            aa.setArtistOrder(order++);
            albumArtists.add(aa);
        }
        albumArtistRepository.saveAll(albumArtists);
    }

    private AlbumResponseDTO buildAlbumResponse(Album album, List<Song> songs, String username) {
        List<AlbumArtist> albumArtistEntities = albumArtistRepository.findByAlbumWithArtists(album);
        List<Artist> artistEntities = albumArtistEntities.stream()
                .map(AlbumArtist::getArtist)
                .toList();
        boolean isLiked = albumLikeRepository.existsByAlbumIdAndUsername(
                album.getId(), username
        );

        return new AlbumResponseDTO(
                album.getId(),
                album.getTitle(),
                album.getDescription(),
                album.getImageKey(),
                album.getReleaseDate(),
                artistMapper.toListDto(artistEntities),
                songMapper.toListDto(songs),
                isLiked
        );
    }
}
