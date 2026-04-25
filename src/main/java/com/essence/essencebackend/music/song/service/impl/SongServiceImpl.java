package com.essence.essencebackend.music.song.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.library.like.repository.SongLikeRepository;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.service.AlbumOfSongService;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.service.ArtistOfSongService;
import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.model.embedded.SongArtistId;
import com.essence.essencebackend.music.shared.service.UrlBuilder;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.dto.SongSyncRequestDTO;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.mapper.SongMapperByInfo;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.model.SongArtist;
import com.essence.essencebackend.music.song.repository.SongRepository;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import com.essence.essencebackend.music.song.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class SongServiceImpl implements SongService {

    private final UrlBuilder urlBuilder;
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final AlbumOfSongService albumOfSongService;
    private final ArtistOfSongService artistOfSongService;
    private final UrlExtractor urlExtractor;
    private final SongMapperByInfo songMapperByInfo;
    private final Optional<StreamingService> streamingService;
    private final SongLikeRepository songLikeRepository;

    private static final int MAX_STREAMING_URL_RETRIES = 3;
    private static final int STREAMING_URL_VALIDITY_MINUTES = 300;
    private static final int ALBUM_SEARCH_RETRY_DAYS = 7;

    private record ArtistAlbumResult(Set<Artist> artists, Album album) {}

    @Override
    public SongResponseDTO getSongId(String songUrlOrId, String username, boolean forceRefresh) {
        log.info("Obteniendo canción por la UrlId: {}", songUrlOrId);

        String songUrlId = urlExtractor.resolverId(songUrlOrId, ContentType.SONG);

        Song song = findExistingByUrlId(songUrlId)
                .orElseThrow(() -> new SongNotFoundException(songUrlId));

        song = refreshUrlIfNeeded(song, forceRefresh);
        return buildResponseWithLike(song, username);

        // TODO: Habilitar cuando tengamos IP dedicada — extracción NewPipe server-side
        // String songUrl = urlBuilder.resolveUrl(songUrlOrId, ContentType.SONG);
        // try {
        //     StreamInfo info = StreamInfo.getInfo(streamingService.get(), songUrl);
        //     ArtistAlbumResult result = resolveArtistAndAlbum(
        //             info.getName(), info.getUploaderUrl(), info.getUploaderName());
        //     Song song = createSongFromInfo(info, songUrlId, result.album(), result.artists());
        //     return buildResponseWithLike(song, username);
        // } catch (DataIntegrityViolationException e) {
        //     log.info("Canción ya creada por otro request concurrente: {}", songUrlId);
        //     return findExistingByUrlId(songUrlId)
        //             .map(s -> buildResponseWithLike(refreshUrlIfNeeded(s, forceRefresh), username))
        //             .orElseThrow(ExtractionServiceUnavailableException::new);
        // } catch (Exception e) {
        //     log.error("NewPipe falló para getSongId: {}", e.getMessage());
        //     throw new ExtractionServiceUnavailableException();
        // }
    }

    @Override
    public Song getOrCreateSong(String songUrlOrId) {
        log.info("getOrCreateSong: {}", songUrlOrId);

        String songUrlId = urlExtractor.resolverId(songUrlOrId, ContentType.SONG);
        String songUrl = urlBuilder.resolveUrl(songUrlOrId, ContentType.SONG);

        Optional<Song> existingSong = findExistingByUrlId(songUrlId);
        if (existingSong.isPresent()) {
            return refreshUrlIfNeeded(existingSong.get(), false);
        }

        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), songUrl);
            ArtistAlbumResult result = resolveArtistAndAlbum(
                    info.getName(), info.getUploaderUrl(), info.getUploaderName());
            return createSongFromInfo(info, songUrlId, result.album(), result.artists());
        } catch (DataIntegrityViolationException e) {
            log.info("Canción ya creada concurrentemente: {}", songUrlId);
            return findExistingByUrlId(songUrlId)
                    .map(song -> refreshUrlIfNeeded(song, false))
                    .orElseThrow(ExtractionServiceUnavailableException::new);
        } catch (Exception e) {
            log.error("NewPipe falló para getOrCreateSong: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }

    @Override
    public Song getOrCreateSongFromAlbum(StreamInfoItem item, Album album) {
        String songUrlId = urlExtractor.extractId(item.getUrl(), ContentType.SONG);

        Optional<Song> existingSong = songRepository.findByHlsMasterKey(songUrlId);
        if (existingSong.isPresent()) {
            return refreshUrlIfNeeded(existingSong.get(), false);
        }

        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), item.getUrl());
            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(
                    info.getUploaderUrl(), info.getUploaderName());
            return createSongFromInfo(info, songUrlId, album, artists);
        } catch (DataIntegrityViolationException e) {
            log.info("Canción desde album ya creada concurrentemente: {}", songUrlId);
            return songRepository.findByHlsMasterKey(songUrlId)
                    .map(song -> refreshUrlIfNeeded(song, false))
                    .orElseThrow(ExtractionServiceUnavailableException::new);
        } catch (Exception e) {
            log.error("NewPipe falló para getOrCreateSongFromAlbum: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }

    @Override
    public SongResponseDTO syncSongFromClient(SongSyncRequestDTO request, String username) {
        log.info("Sync desde cliente para videoId: {}", request.videoId());

        Optional<Song> existing = findExistingByUrlId(request.videoId());
        if (existing.isPresent()) {
            Song song = existing.get();
            tryAssignAlbumIfMissing(song, request.title(), request.uploaderUrl(), request.uploaderName());
            return buildResponseWithLike(song, username);
        }

        try {
            ArtistAlbumResult result = resolveArtistAndAlbum(
                    request.title(), request.uploaderUrl(), request.uploaderName());

            Song song = songMapperByInfo.mapFromClientSync(request);
            song.setAlbum(result.album());
            song.setAlbumSearchedAt(Instant.now());

            Song savedSong = persistSongWithArtists(song, result.artists());
            log.info("Song creada desde cliente: '{}' - {}", request.title(), request.videoId());
            return buildResponseWithLike(savedSong, username);
        } catch (DataIntegrityViolationException e) {
            log.info("Song ya creada concurrentemente: {}", request.videoId());
            return findExistingByUrlId(request.videoId())
                    .map(song -> buildResponseWithLike(song, username))
                    .orElseThrow(ExtractionServiceUnavailableException::new);
        }
    }

    @Override
    public SongResponseDTO refreshStreamingUrl(String videoId, String streamingUrl, String username) {
        log.info("Refresh streaming URL desde cliente: {}", videoId);

        Song song = findExistingByUrlId(videoId)
                .orElseThrow(ExtractionServiceUnavailableException::new);

        if (streamingUrl != null && !streamingUrl.isBlank()) {
            song.setStreamingUrl(streamingUrl);
            song.setLastSyncedAt(Instant.now());
            song = songRepository.save(song);
            log.info("StreamingUrl actualizada desde cliente: {}", videoId);
        } else {
            song = refreshUrlIfNeeded(song, true);
        }

        return buildResponseWithLike(song, username);
    }

    private Optional<Song> findExistingByUrlId(String urlId) {
        return songRepository.findByHlsMasterKeyWithRelations(urlId);
    }

    private ArtistAlbumResult resolveArtistAndAlbum(
            String songTitle, String uploaderUrl, String uploaderName) {
        Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(uploaderUrl, uploaderName);
        Artist principal = artists.iterator().next();
        Album album = albumOfSongService.getOrCreateAlbumBySong(
                songTitle, principal.getNameArtist(), artists);
        return new ArtistAlbumResult(artists, album);
    }

    private void tryAssignAlbumIfMissing(Song song, String songTitle, String uploaderUrl, String uploaderName) {
        if (song.getAlbum() != null) return;
        if (!shouldRetryAlbumSearch(song)) return;

        log.info("Reintentando búsqueda de album para: '{}'", songTitle);
        try {
            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(uploaderUrl, uploaderName);
            Artist principal = artists.iterator().next();
            Album album = albumOfSongService.getOrCreateAlbumBySong(
                    songTitle, principal.getNameArtist(), artists);

            song.setAlbumSearchedAt(Instant.now());
            if (album != null) {
                song.setAlbum(album);
                log.info("Album asignado en reintento: '{}'", album.getTitle());
            } else {
                log.info("Album no encontrado en reintento para: '{}'", songTitle);
            }
            songRepository.save(song);
        } catch (Exception e) {
            log.error("Error reintentando album para '{}': {}", songTitle, e.getMessage());
        }
    }

    private boolean shouldRetryAlbumSearch(Song song) {
        if (song.getAlbumSearchedAt() == null) return true;
        return song.getAlbumSearchedAt()
                .isBefore(Instant.now().minus(Duration.ofDays(ALBUM_SEARCH_RETRY_DAYS)));
    }

    private Song persistSongWithArtists(Song song, Set<Artist> artists) {
        Song savedSong = songRepository.save(song);
        List<SongArtist> songArtists = createSongArtists(savedSong, artists);
        savedSong.setSongArtists(songArtists);
        return songRepository.save(savedSong);
    }

    private Song createSongFromInfo(StreamInfo info, String songUrlId, Album album, Set<Artist> artists) {
        String streamingUrl = extractBestStreamingUrl(info);
        if (streamingUrl == null) {
            String songUrl = urlBuilder.build(songUrlId, ContentType.SONG);
            streamingUrl = retryStreamingUrlExtraction(songUrl);
        }
        Song song = songMapperByInfo.mapToSong(info, streamingUrl, songUrlId);
        song.setAlbum(album);
        return persistSongWithArtists(song, artists);
    }

    private List<SongArtist> createSongArtists(Song song, Set<Artist> artists) {
        List<SongArtist> songArtists = new ArrayList<>();
        int order = 0;
        for (Artist artist : artists) {
            SongArtist sa = new SongArtist();
            sa.setId(new SongArtistId(song.getId(), artist.getId()));
            sa.setSong(song);
            sa.setArtist(artist);
            sa.setIsPrimary(order == 0);
            sa.setArtistOrder(order++);
            songArtists.add(sa);
        }
        return songArtists;
    }




    private Song refreshUrlIfNeeded(Song song, boolean forceRefresh) {
        log.info("Verificando si url está vigente: {}", song.getHlsMasterKey());
        if (!needsStreamingRefresh(song, forceRefresh)) {
            log.info("Url vigente para canción: {}", song.getHlsMasterKey());
            return song;
        }
        log.warn("Url vencida, refrescando: {}", song.getHlsMasterKey());
        String newUrl = getUrlValidWithRetry(song.getHlsMasterKey());
        if (newUrl == null || newUrl.isBlank()) {
            log.warn("No se pudo refrescar streamingUrl: {}", song.getHlsMasterKey());
            song.setStreamingUrl(null);
            return songRepository.save(song);
        }
        song.setStreamingUrl(newUrl);
        song.setLastSyncedAt(Instant.now());
        return songRepository.save(song);
    }

    private boolean needsStreamingRefresh(Song song, boolean forceRefresh) {
        if (forceRefresh) return true;
        if (song.getStreamingUrl() == null || song.getStreamingUrl().isBlank()) return true;
        return !isUrlValid(song.getLastSyncedAt());
    }

    private boolean isUrlValid(Instant lastSynced) {
        if (lastSynced == null) return false;
        long minutesAgo = Duration.between(lastSynced, Instant.now()).toMinutes();
        return minutesAgo < STREAMING_URL_VALIDITY_MINUTES;
    }

    private String extractBestStreamingUrl(StreamInfo info) {
        return info.getAudioStreams().stream()
                .max(Comparator.comparing(AudioStream::getBitrate))
                .map(AudioStream::getUrl)
                .orElse(null);
    }

    private String getUrlValid(String hlsMasterKey) {
        String streamingUrlId = urlBuilder.build(hlsMasterKey, ContentType.SONG);
        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), streamingUrlId);
            return extractBestStreamingUrl(info);
        } catch (Exception e) {
            log.warn("NewPipe falló al refrescar URL: {}", e.getMessage());
            return null;
        }
    }

    private String getUrlValidWithRetry(String hlsMasterKey) {
        String url = getUrlValid(hlsMasterKey);
        if (url != null) return url;
        String songUrl = urlBuilder.build(hlsMasterKey, ContentType.SONG);
        return retryStreamingUrlExtraction(songUrl);
    }

    private String retryStreamingUrlExtraction(String songUrl) {
        for (int attempt = 2; attempt <= MAX_STREAMING_URL_RETRIES; attempt++) {
            log.warn("Reintentando streaming URL (intento {}/{}): {}", attempt, MAX_STREAMING_URL_RETRIES, songUrl);
            try {
                StreamInfo retryInfo = StreamInfo.getInfo(streamingService.get(), songUrl);
                String retryUrl = extractBestStreamingUrl(retryInfo);
                if (retryUrl != null) return retryUrl;
            } catch (Exception e) {
                log.error("Error en reintento {}: {}", attempt, e.getMessage());
            }
        }
        return null;
    }

    private SongResponseDTO buildResponseWithLike(Song song, String username) {
        boolean isLiked = songLikeRepository.existsBySongIdAndUsername(song.getId(), username);
        return songMapper.toFullDto(song, isLiked);
    }
}