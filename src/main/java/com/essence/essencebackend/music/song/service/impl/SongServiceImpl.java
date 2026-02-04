package com.essence.essencebackend.music.song.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.music.shared.dto.IdStreamingRequestDTO;
import com.essence.essencebackend.music.shared.service.UrlBuilder;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import com.essence.essencebackend.music.song.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class SongServiceImpl implements SongService {

    private final UrlBuilder urlBuilder;
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    private final Optional<StreamingService> streamingService;

    @Override
    public SongResponseDTO getSongId(IdStreamingRequestDTO data, String username) {
        log.info("Obteniendo canción por la id: {}, por el usuario: {}", data.id() , username);

        String streamingUrlId = urlBuilder.build(data.id(), UrlBuilder.ContentType.SONG);

        Optional<Song> existingSong = songRepository.findByExternalId(data.id());

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
                    streamingService.get(),streamingUrlId
            );
            String streamingUrl = info.getAudioStreams().stream()
                    .max(Comparator.comparing(AudioStream::getBitrate))
                    .map(AudioStream::getUrl)
                    .orElse(null);
            Song song = mapToSong(info, streamingUrl, data.id());
            songRepository.save(song);
            return songMapper.toDto(song);
        } catch (Exception e) {
            log.error("Error obteniendo metadata de canción: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }

    private Song mapToSong(StreamInfo info,String streamingUrl, String externalId) {
        Song song = new Song();
        song.setTitle(info.getName());
        song.setDurationMs((int) info.getDuration() * 1000);
        song.setHlsMasterKey(externalId);
        song.setStreamingUrl(streamingUrl);
        song.setImageKey(info.getThumbnails().isEmpty() ? null
                : info.getThumbnails().get(0).getUrl());
        song.setTotalStreams(info.getViewCount());
        song.setLastSyncedAt(Instant.now());
        song.setStatus("ACTIVE");
        return song;
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
