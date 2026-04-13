package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.dto.InvidiousStreamResponse;
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
    private final PipedStreamExtractor extractor;
    private final ArtistOfSongService artistOfSongService;
    private final AlbumOfSongService albumOfSongService;

    public Optional<String> getStreamingUrl(String videoId) {
        log.info("Fallback: obteniendo streaming URL para videoId: {}", videoId);

        // 1. intentar Piped
        Optional<String> pipedUrl = pipedApiClient.getStreamFromPiped(videoId)
                .flatMap(extractor::extractBestAudioUrl);
        if (pipedUrl.isPresent()) {
            log.info("Streaming URL obtenida desde Piped para: {}", videoId);
            return pipedUrl;
        }

        // 2. intentar Invidious
        Optional<String> invidiousUrl = pipedApiClient.getStreamFromInvidious(videoId)
                .flatMap(extractor::extractBestAudioUrl);
        if (invidiousUrl.isPresent()) {
            log.info("Streaming URL obtenida desde Invidious para: {}", videoId);
            return invidiousUrl;
        }

        log.error("Ni Piped ni Invidious pudieron obtener streaming URL para: {}", videoId);
        return Optional.empty();
    }

    public Optional<Song> createSongFromPiped(String videoId) {
        log.info("Fallback: creando canción completa para videoId: {}", videoId);

        // 1. intentar Piped
        Optional<PipedStreamResponse> pipedResponse = pipedApiClient.getStreamFromPiped(videoId);
        if (pipedResponse.isPresent()) {
            log.info("Usando Piped para crear canción: {}", videoId);
            return buildSongFromPiped(pipedResponse.get(), videoId);
        }

        // 2. intentar Invidious
        Optional<InvidiousStreamResponse> invidiousResponse = pipedApiClient.getStreamFromInvidious(videoId);
        if (invidiousResponse.isPresent()) {
            log.info("Usando Invidious para crear canción: {}", videoId);
            return buildSongFromInvidious(invidiousResponse.get(), videoId);
        }

        log.error("Ni Piped ni Invidious pudieron obtener metadata para: {}", videoId);
        return Optional.empty();
    }

    private Optional<Song> buildSongFromPiped(PipedStreamResponse response, String videoId) {
        try {
            String streamingUrl = extractor.extractBestAudioUrl(response).orElse(null);
            String uploaderUrl = extractor.extractUploaderUrl(response);
            String uploaderName = extractor.extractUploaderName(response);

            if (uploaderUrl == null || uploaderName == null) {
                log.error("Piped: metadata de artista incompleta para videoId: {}", videoId);
                return Optional.empty();
            }

            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(uploaderUrl, uploaderName);
            Artist principal = artists.iterator().next();
            String title = extractor.extractTitle(response);
            Album album = albumOfSongService.getOrCreateAlbumBySong(title, principal.getNameArtist(), artists);

            Song song = new Song();
            song.setTitle(title);
            song.setDurationMs(extractor.extractDurationMs(response));
            song.setHlsMasterKey(videoId);
            song.setStreamingUrl(streamingUrl);
            song.setImageKey(extractor.extractThumbnailUrl(response));
            song.setTotalStreams(extractor.extractViewCount(response));
            song.setReleaseDate(extractor.extractUploadDate(response));
            song.setLastSyncedAt(Instant.now());
            song.setStatus(SongStatus.ACTIVE);
            song.setAlbum(album);

            log.info("Piped: canción creada: '{}' - {}", title, videoId);
            return Optional.of(song);
        } catch (Exception e) {
            log.error("Piped: error creando canción {}: {}", videoId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private Optional<Song> buildSongFromInvidious(InvidiousStreamResponse response, String videoId) {
        try {
            String streamingUrl = extractor.extractBestAudioUrl(response).orElse(null);
            String uploaderUrl = extractor.extractUploaderUrl(response);
            String uploaderName = extractor.extractUploaderName(response);

            if (uploaderUrl == null || uploaderName == null) {
                log.error("Invidious: metadata de artista incompleta para videoId: {}", videoId);
                return Optional.empty();
            }

            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(uploaderUrl, uploaderName);
            Artist principal = artists.iterator().next();
            String title = extractor.extractTitle(response);
            Album album = albumOfSongService.getOrCreateAlbumBySong(title, principal.getNameArtist(), artists);

            Song song = new Song();
            song.setTitle(title);
            song.setDurationMs(extractor.extractDurationMs(response));
            song.setHlsMasterKey(videoId);
            song.setStreamingUrl(streamingUrl);
            song.setImageKey(extractor.extractThumbnailUrl(response));
            song.setTotalStreams(extractor.extractViewCount(response));
            song.setReleaseDate(extractor.extractUploadDate(response));
            song.setLastSyncedAt(Instant.now());
            song.setStatus(SongStatus.ACTIVE);
            song.setAlbum(album);

            log.info("Invidious: canción creada: '{}' - {}", title, videoId);
            return Optional.of(song);
        } catch (Exception e) {
            log.error("Invidious: error creando canción {}: {}", videoId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}