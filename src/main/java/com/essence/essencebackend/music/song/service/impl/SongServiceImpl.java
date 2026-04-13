package com.essence.essencebackend.music.song.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.extractor.service.PipedApiService;
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
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.mapper.SongMapperByInfo;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.model.SongArtist;
import com.essence.essencebackend.music.song.repository.SongRepository;
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
import java.util.stream.Collectors;

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
    private final PipedApiService pipedApiService;

    private static final int MAX_STREAMING_URL_RETRIES = 3;
    private static final int STREAMING_URL_VALIDITY_MINUTES = 45;

    @Override
    public SongResponseDTO getSongId(String songUrlOrId, String username, boolean forceRefresh) {
        log.info("Obteniendo canción por la UrlId: {}", songUrlOrId);

        String songUrlId = urlExtractor.resolverId(songUrlOrId, ContentType.SONG);
        String songUrl = urlBuilder.resolveUrl(songUrlOrId, ContentType.SONG);

        Optional<Song> existingSong = songRepository.findByHlsMasterKeyWithRelations(songUrlId);
        if (existingSong.isPresent()) {
            Song song = refreshUrlIfNeeded(existingSong.get(), forceRefresh);
            return buildResponseWithLike(song, username);
        }

        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), songUrl);
            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(
                    info.getUploaderUrl(), info.getUploaderName());
            Artist principal = artists.iterator().next();
            Album album = albumOfSongService.getOrCreateAlbumBySong(
                    info.getName(), principal.getNameArtist(), artists);
            Song song = createSongFromInfo(info, songUrlId, album, artists);
            return buildResponseWithLike(song, username);
        } catch (DataIntegrityViolationException e) {
            log.info("Canción ya creada por otro request concurrente, re-fetching: {}", songUrlId);
            return songRepository.findByHlsMasterKeyWithRelations(songUrlId)
                    .map(song -> buildResponseWithLike(refreshUrlIfNeeded(song, forceRefresh), username))
                    .orElseThrow(ExtractionServiceUnavailableException::new);
        } catch (Exception e) {
            log.warn("NewPipe falló para getSongId, intentando Piped: {}", e.getMessage());
            try {
                return createSongViaPipedFallback(songUrlId, username);
            } catch (Exception pipedEx) {
                log.error("Piped también falló para getSongId: {}", pipedEx.getMessage());
                throw new ExtractionServiceUnavailableException();
            }
        }
    }

    @Override
    public Song getOrCreateSong(String songUrlOrId) {
        log.info("getOrCreateSong: buscando o creando canción por: {}", songUrlOrId);

        String songUrlId = urlExtractor.resolverId(songUrlOrId, ContentType.SONG);
        String songUrl = urlBuilder.resolveUrl(songUrlOrId, ContentType.SONG);

        Optional<Song> existingSong = songRepository.findByHlsMasterKeyWithRelations(songUrlId);
        if (existingSong.isPresent()) {
            return refreshUrlIfNeeded(existingSong.get(), false);
        }

        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), songUrl);
            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(
                    info.getUploaderUrl(), info.getUploaderName());
            Artist principal = artists.iterator().next();
            Album album = albumOfSongService.getOrCreateAlbumBySong(
                    info.getName(), principal.getNameArtist(), artists);
            return createSongFromInfo(info, songUrlId, album, artists);
        } catch (DataIntegrityViolationException e) {
            log.info("Canción ya creada por otro request concurrente, re-fetching: {}", songUrlId);
            return songRepository.findByHlsMasterKeyWithRelations(songUrlId)
                    .map(song -> refreshUrlIfNeeded(song, false))
                    .orElseThrow(ExtractionServiceUnavailableException::new);
        } catch (Exception e) {
            log.warn("NewPipe falló en getOrCreateSong, intentando Piped: {}", e.getMessage());
            try {
                return pipedApiService.createSongFromPiped(songUrlId)
                        .map(this::saveSongWithArtists)
                        .orElseThrow(ExtractionServiceUnavailableException::new);
            } catch (Exception pipedEx) {
                log.error("Piped también falló en getOrCreateSong: {}", pipedEx.getMessage());
                throw new ExtractionServiceUnavailableException();
            }
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
            log.info("Canción desde album ya creada por otro request, re-fetching: {}", songUrlId);
            return songRepository.findByHlsMasterKey(songUrlId)
                    .map(song -> refreshUrlIfNeeded(song, false))
                    .orElseThrow(ExtractionServiceUnavailableException::new);
        } catch (Exception e) {
            log.warn("NewPipe falló en getOrCreateSongFromAlbum, intentando Piped: {}", e.getMessage());
            try {
                return pipedApiService.createSongFromPiped(songUrlId)
                        .map(song -> {
                            song.setAlbum(album);
                            return saveSongWithArtists(song);
                        })
                        .orElseThrow(ExtractionServiceUnavailableException::new);
            } catch (Exception pipedEx) {
                log.error("Piped también falló en getOrCreateSongFromAlbum: {}", pipedEx.getMessage());
                throw new ExtractionServiceUnavailableException();
            }
        }
    }

    private Song refreshUrlIfNeeded(Song song, boolean forceRefresh) {
        log.info("Verificando si url está vigente: {}", song.getHlsMasterKey());
        if (!needsStreamingRefresh(song, forceRefresh)) {
            log.info("Url vigente para canción: {}", song.getHlsMasterKey());
            return song;
        }
        log.warn("Url vencida, nula o forzada, refrescando: {}", song.getHlsMasterKey());
        String newUrl = getUrlValidWithRetry(song.getHlsMasterKey());
        if (newUrl == null || newUrl.isBlank()) {
            log.warn("No se pudo refrescar streamingUrl para: {}", song.getHlsMasterKey());
            song.setStreamingUrl(null);
            return songRepository.save(song);
        }
        song.setStreamingUrl(newUrl);
        song.setLastSyncedAt(Instant.now());
        return songRepository.save(song);
    }

    private boolean needsStreamingRefresh(Song song, boolean forceRefresh) {
        if (forceRefresh) {
            return true;
        }
        if (song.getStreamingUrl() == null || song.getStreamingUrl().isBlank()) {
            return true;
        }
        return !isUrlValid(song.getLastSyncedAt());
    }

    private Song createSongFromInfo(StreamInfo info, String songUrlId, Album album, Set<Artist> artists) {
        String streamingUrl = extractBestStreamingUrl(info);
        if (streamingUrl == null) {
            String songUrl = urlBuilder.build(songUrlId, ContentType.SONG);
            streamingUrl = retryStreamingUrlExtraction(songUrl);
        }
        Song song = songMapperByInfo.mapToSong(info, streamingUrl, songUrlId);
        song.setAlbum(album);
        Song savedSong = songRepository.save(song);
        List<SongArtist> songArtists = createSongArtists(savedSong, artists);
        savedSong.setSongArtists(songArtists);
        return songRepository.save(savedSong);
    }

    private Song saveSongWithArtists(Song song) {
        Song savedSong = songRepository.save(song);
        Set<Artist> artists = song.getAlbum().getAlbumArtists().stream()
                .map(aa -> aa.getArtist())
                .collect(Collectors.toSet());
        savedSong.setSongArtists(createSongArtists(savedSong, artists));
        return songRepository.save(savedSong);
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

    private String extractBestStreamingUrl(StreamInfo info) {
        return info.getAudioStreams().stream()
                .max(Comparator.comparing(AudioStream::getBitrate))
                .map(AudioStream::getUrl)
                .orElse(null);
    }

    private String getUrlValid(String hlsMasterKey) {
        log.info("Obteniendo nueva url válida");
        String streamingUrlId = urlBuilder.build(hlsMasterKey, ContentType.SONG);
        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), streamingUrlId);
            return extractBestStreamingUrl(info);
        } catch (Exception e) {
            log.warn("NewPipe falló al refrescar URL, intentando Piped: {}", e.getMessage());
            return pipedApiService.getStreamingUrl(hlsMasterKey).orElse(null);
        }
    }

    private String getUrlValidWithRetry(String hlsMasterKey) {
        String url = getUrlValid(hlsMasterKey);
        if (url != null) {
            return url;
        }
        String songUrl = urlBuilder.build(hlsMasterKey, ContentType.SONG);
        return retryStreamingUrlExtraction(songUrl);
    }

    private String retryStreamingUrlExtraction(String songUrl) {
        for (int attempt = 2; attempt <= MAX_STREAMING_URL_RETRIES; attempt++) {
            log.warn("StreamingUrl null, reintentando (intento {}/{}): {}", attempt, MAX_STREAMING_URL_RETRIES, songUrl);
            try {
                StreamInfo retryInfo = StreamInfo.getInfo(streamingService.get(), songUrl);
                String retryUrl = extractBestStreamingUrl(retryInfo);
                if (retryUrl != null) {
                    log.info("StreamingUrl obtenida en intento {}: {}", attempt, songUrl);
                    return retryUrl;
                }
            } catch (Exception e) {
                log.error("Error en reintento {} obteniendo streaming URL: {}", attempt, e.getMessage());
            }
        }
        log.error("No se pudo obtener streamingUrl después de {} intentos para: {}", MAX_STREAMING_URL_RETRIES, songUrl);
        return null;
    }

    private boolean isUrlValid(Instant lastSynced) {
        if (lastSynced == null) {
            return false;
        }
        long minutesAgo = Duration.between(lastSynced, Instant.now()).toMinutes();
        return minutesAgo < STREAMING_URL_VALIDITY_MINUTES;
    }

    private SongResponseDTO buildResponseWithLike(Song song, String username) {
        boolean isLiked = songLikeRepository.existsBySongIdAndUsername(
                song.getId(), username);
        return songMapper.toFullDto(song, isLiked);
    }

    private SongResponseDTO createSongViaPipedFallback(String songUrlId, String username) {
        Optional<Song> pipedSong = pipedApiService.createSongFromPiped(songUrlId);
        if (pipedSong.isEmpty()) {
            throw new ExtractionServiceUnavailableException();
        }
        Song savedSong = saveSongWithArtists(pipedSong.get());
        return buildResponseWithLike(savedSong, username);
    }
}