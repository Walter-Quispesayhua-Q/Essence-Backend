package com.essence.essencebackend.music.song.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.service.AlbumOfSongService;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.service.ArtistOfSongService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public SongResponseDTO getSongId(String songUrlOrId) {
        log.info("Obteniendo canción por la UrlId: {}", songUrlOrId);

        String songUrlId = urlExtractor.resolverId(songUrlOrId, UrlExtractor.ContentType.SONG);

        String songUrl = urlBuilder.resolveUrl(songUrlOrId, UrlBuilder.ContentType.SONG);

        Optional<Song> existingSong = songRepository.findByHlsMasterKeyWithRelations(songUrlId);
        if (existingSong.isPresent()) {
            Song song = refreshUrlIfNeeded(existingSong.get());
            return songMapper.toDto(song);
        }
        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), songUrl);
            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(
                    info.getUploaderUrl(), info.getUploaderName());
            Artist principal = artists.iterator().next();
            Album album = albumOfSongService.getOrCreateAlbumBySong(
                    info.getName(), principal.getNameArtist(), artists);
            Song song = createSongFromInfo(info, songUrlId, album, artists);
            return songMapper.toDto(song);
        } catch (Exception e) {
            log.error("Error obteniendo metadata de canción: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }
    @Override
    public Song getOrCreateSongFromAlbum(StreamInfoItem item, Album album) {
        String songUrlId = urlExtractor.extractId(item.getUrl(), UrlExtractor.ContentType.SONG);
        Optional<Song> existingSong = songRepository.findByHlsMasterKey(songUrlId);
        if (existingSong.isPresent()) {
            return refreshUrlIfNeeded(existingSong.get());
        }
        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), item.getUrl());
            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(
                    info.getUploaderUrl(), info.getUploaderName());
            return createSongFromInfo(info, songUrlId, album, artists);
        } catch (Exception e) {
            log.error("Error creando canción desde album: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }

    private Song refreshUrlIfNeeded(Song song) {
        log.info("Verificando si url está vigente: {}", song.getHlsMasterKey());
        if (isUrlValid(song.getLastSyncedAt())) {
            log.info("Url vigente para canción: {}", song.getHlsMasterKey());
            return song;
        }
        log.warn("Url vencida, refrescando: {}", song.getHlsMasterKey());
        String newUrl = getUrlValid(song.getHlsMasterKey());
        song.setStreamingUrl(newUrl);
        song.setLastSyncedAt(Instant.now());
        return songRepository.save(song);
    }
    private Song createSongFromInfo(StreamInfo info, String songUrlId, Album album, Set<Artist> artists) {
        String streamingUrl = extractBestStreamingUrl(info);
        Song song = songMapperByInfo.mapToSong(info, streamingUrl, songUrlId);
        song.setAlbum(album);
        Song savedSong = songRepository.save(song);
        List<SongArtist> songArtists = createSongArtists(savedSong, artists);
        savedSong.setSongArtists(songArtists);
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
        String streamingUrlId = urlBuilder.build(hlsMasterKey, UrlBuilder.ContentType.SONG);
        try {
            StreamInfo info = StreamInfo.getInfo(streamingService.get(), streamingUrlId);
            return extractBestStreamingUrl(info);
        } catch (Exception e) {
            log.error("Error obteniendo streaming URL: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }
    private boolean isUrlValid(Instant lastSynced) {
        if (lastSynced == null) return false;
        long hoursAgo = Duration.between(lastSynced, Instant.now()).toHours();
        return hoursAgo < 6;
    }
}
