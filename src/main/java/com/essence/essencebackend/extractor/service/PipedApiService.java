package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.dto.PipedStreamResponse;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.service.AlbumOfSongService;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.service.ArtistOfSongService;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.model.SongStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class PipedApiService {

    private final PipedApiClient pipedApiClient;
    private final PipedStreamExtractor pipedStreamExtractor;
    private final ArtistOfSongService artistOfSongService;
    private final AlbumOfSongService albumOfSongService;

    public Optional<String> getStreamingUrl(String videoId) {
        log.info("Piped: obteniendo streaming URL para videoId: {}", videoId);
        return pipedApiClient.getStreamInfo(videoId)
                .flatMap(pipedStreamExtractor::extractBestAudioUrl);
    }

    public Optional<Song> createSongFromPiped(String videoId) {
        log.info("Piped: creando canción completa para videoId: {}", videoId);

        Optional<PipedStreamResponse> responseOpt = pipedApiClient.getStreamInfo(videoId);
        if (responseOpt.isEmpty()) {
            log.error("Piped: no se pudo obtener metadata para videoId: {}", videoId);
            return Optional.empty();
        }

        PipedStreamResponse response = responseOpt.get();

        try {
            String streamingUrl = pipedStreamExtractor.extractBestAudioUrl(response)
                    .orElse(null);

            String uploaderUrl = pipedStreamExtractor.extractUploaderUrl(response);
            String uploaderName = pipedStreamExtractor.extractUploaderName(response);

            if (uploaderUrl == null || uploaderName == null) {
                log.error("Piped: metadata de artista incompleta para videoId: {}", videoId);
                return Optional.empty();
            }

            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(
                    uploaderUrl, uploaderName);
            Artist principal = artists.iterator().next();

            String title = pipedStreamExtractor.extractTitle(response);
            Album album = albumOfSongService.getOrCreateAlbumBySong(
                    title, principal.getNameArtist(), artists);

            Song song = new Song();
            song.setTitle(title);
            song.setDurationMs(pipedStreamExtractor.extractDurationMs(response));
            song.setHlsMasterKey(videoId);
            song.setStreamingUrl(streamingUrl);
            song.setImageKey(pipedStreamExtractor.extractThumbnailUrl(response));
            song.setTotalStreams(pipedStreamExtractor.extractViewCount(response));
            song.setReleaseDate(pipedStreamExtractor.extractUploadDate(response));
            song.setLastSyncedAt(Instant.now());
            song.setStatus(SongStatus.ACTIVE);
            song.setAlbum(album);

            log.info("Piped: canción creada exitosamente: '{}' - {}", title, videoId);
            return Optional.of(song);

        } catch (Exception e) {
            log.error("Piped: error creando canción para videoId {}: {}",
                    videoId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}