package com.essence.essencebackend.extractor.service;

import com.essence.essencebackend.extractor.dto.InvidiousStreamResponse;
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
public class InvidiousApiService {

    private final InvidiousApiClient apiClient;
    private final InvidiousStreamExtractor extractor;
    private final ArtistOfSongService artistOfSongService;
    private final AlbumOfSongService albumOfSongService;

    /** Obtener solo la streaming URL para refrescar */
    public Optional<String> getStreamingUrl(String videoId) {
        log.info("Invidious: obteniendo streaming URL para: {}", videoId);

        return apiClient.getStream(videoId)
                .flatMap(extractor::extractBestAudioUrl);
    }

    /** Crear canción completa desde Invidious (fallback del servidor) */
    public Optional<Song> createSong(String videoId) {
        log.info("Invidious: creando canción para videoId: {}", videoId);

        Optional<InvidiousStreamResponse> response = apiClient.getStream(videoId);
        if (response.isEmpty()) {
            log.error("Invidious: no se pudo obtener metadata para: {}", videoId);
            return Optional.empty();
        }

        return buildSong(response.get(), videoId);
    }

    private Optional<Song> buildSong(InvidiousStreamResponse response, String videoId) {
        try {
            String streamingUrl = extractor.extractBestAudioUrl(response).orElse(null);
            String uploaderUrl = extractor.extractUploaderUrl(response);
            String uploaderName = extractor.extractUploaderName(response);

            if (uploaderUrl == null || uploaderName == null) {
                log.error("Invidious: metadata de artista incompleta para: {}", videoId);
                return Optional.empty();
            }

            Set<Artist> artists = artistOfSongService.getOrCreateArtistBySong(uploaderUrl, uploaderName);
            Artist principal = artists.iterator().next();
            String title = extractor.extractTitle(response);
            Album album = albumOfSongService.getOrCreateAlbumBySong(
                    title, principal.getNameArtist(), artists);

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