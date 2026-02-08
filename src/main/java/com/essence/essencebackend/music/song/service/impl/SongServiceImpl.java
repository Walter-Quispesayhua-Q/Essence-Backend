package com.essence.essencebackend.music.song.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.service.AlbumOfSongService;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.service.ArtistOfSongService;
import com.essence.essencebackend.music.shared.dto.IdStreamingRequestDTO;
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
    public SongResponseDTO getSongId(IdStreamingRequestDTO data) {
        log.info("Obteniendo canción por la id: {}, por el usuario:", data.id() );

        String songUrl = urlBuilder.build(data.id(), UrlBuilder.ContentType.SONG);

        Optional<Song> existingSong = songRepository.findByHlsMasterKey(data.id());

        if (existingSong.isPresent()) {
            Song song = existingSong.get();

            log.info("Verificando si url está vigente: {}", song.getHlsMasterKey());
            if (isUrlValid(song.getLastSyncedAt())) {
                log.info("Url vigente para canción: {}", song.getHlsMasterKey());
                return songMapper.toDto(song);
            }
            log.warn("Url vencida, refrescando: {}", song.getHlsMasterKey());

            String newUrlValid = getUrlValid(song.getHlsMasterKey());

            song.setStreamingUrl(newUrlValid);
            song.setLastSyncedAt(Instant.now());
            songRepository.save(song);
            return songMapper.toDto(song);
        }
        try {
            StreamInfo info = StreamInfo.getInfo(
                    streamingService.get(),songUrl
            );

            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(info.getUploaderUrl(), info.getUploaderName());
            Artist principal = artists.iterator().next();
            Album album = albumOfSongService.getOrCreateAlbumBySong(info.getName(), principal.getNameArtist(), artists);
            String streamingUrl = info.getAudioStreams().stream()
                    .max(Comparator.comparing(AudioStream::getBitrate))
                    .map(AudioStream::getUrl)
                    .orElse(null);

            Song song = songMapperByInfo.mapToSong(info, streamingUrl, data.id());
            song.setAlbum(album);
            Song savedSong = songRepository.save(song);
            List<SongArtist> songArtists = new ArrayList<>();
            int order = 0;

            for (Artist artist : artists) {
                SongArtist sa = new SongArtist();
                sa.setId(new SongArtistId(savedSong.getId(), artist.getId()));
                sa.setSong(savedSong);
                sa.setArtist(artist);
                sa.setIsPrimary(order == 0);
                sa.setArtistOrder(order++);
                songArtists.add(sa);
            }
            savedSong.setSongArtists(songArtists);
            songRepository.save(savedSong);

            return songMapper.toDto(savedSong);
        } catch (Exception e) {
            log.error("Error obteniendo metadata de canción: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }

    @Override
    public Song getOrCreateSongFromAlbum(StreamInfoItem item, Album album) {

        String songUrlId = urlExtractor.extractId(item.getUrl(), UrlExtractor.ContentType.SONG);

        Optional<Song> songExits = songRepository.findByHlsMasterKey(songUrlId);
        if (songExits.isPresent()) {
            Song song = songExits.get();
            log.info("Verificando si url está vigente: {}", song.getHlsMasterKey());
            if (isUrlValid(song.getLastSyncedAt())) {
                log.info("Url vigente para canción: {}", song.getHlsMasterKey());
                return song;
            }
            log.warn("Url vencida, refrescando: {}", song.getHlsMasterKey());

            String newUrlValid = getUrlValid(song.getHlsMasterKey());

            song.setStreamingUrl(newUrlValid);
            song.setLastSyncedAt(Instant.now());
            songRepository.save(song);
            return song;
        }

        try {
            StreamInfo info = StreamInfo.getInfo(
                    streamingService.get(),item.getUrl()
            );
            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(info.getUploaderUrl(), info.getUploaderName());

            String streamingUrl = info.getAudioStreams().stream()
                    .max(Comparator.comparing(AudioStream::getBitrate))
                    .map(AudioStream::getUrl)
                    .orElse(null);
            Song song = songMapperByInfo.mapToSong(info, streamingUrl, songUrlId);
            song.setAlbum(album);
            Song savedSong = songRepository.save(song);
            List<SongArtist> songArtists = new ArrayList<>();
            int order = 0;

            for (Artist artist : artists) {
                SongArtist sa = new SongArtist();
                sa.setId(new SongArtistId(savedSong.getId(), artist.getId()));
                sa.setSong(savedSong);
                sa.setArtist(artist);
                sa.setIsPrimary(order == 0);
                sa.setArtistOrder(order++);
                songArtists.add(sa);
            }
            savedSong.setSongArtists(songArtists);
            return songRepository.save(savedSong);
        } catch (Exception e) {
            throw new ExtractionServiceUnavailableException();
        }
    }

    private String getUrlValid(String hlsMasterKey) {
        log.info("Obtenido la nueva url valida");
        String streamingUrlId = urlBuilder.build(hlsMasterKey, UrlBuilder.ContentType.SONG);
        try {
            StreamInfo info = StreamInfo.getInfo(
                    streamingService.get(),
                    streamingUrlId
            );
            return info.getAudioStreams()
                    .stream()
                    .max(Comparator.comparing(AudioStream::getBitrate))
                    .map(AudioStream::getUrl)
                    .orElse(null);
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
