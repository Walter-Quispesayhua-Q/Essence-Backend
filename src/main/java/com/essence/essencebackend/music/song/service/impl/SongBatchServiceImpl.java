package com.essence.essencebackend.music.song.service.impl;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import com.essence.essencebackend.music.song.service.SongBatchService;
import com.essence.essencebackend.music.song.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SongBatchServiceImpl implements SongBatchService {

    private final SongService songService;
    private final SongRepository songRepository;
    private final ExecutorService executor;

    private static final Semaphore NEWPIPE_SEMAPHORE = new Semaphore(3);
    private static final long DELAY_BETWEEN_REQUESTS_MS = 400;

    public SongBatchServiceImpl(
            SongService songService,
            SongRepository songRepository,
            @Qualifier("songBatchExecutor") ExecutorService executor
    ) {
        this.songService = songService;
        this.songRepository = songRepository;
        this.executor = executor;
    }

    @Override
    public List<Song> saveSongsFromAlbum(Album album, List<StreamInfoItem> songItems) {
        List<CompletableFuture<Song>> futures = songItems.stream()
                .map(item -> CompletableFuture.supplyAsync(
                        () -> fetchWithRateLimit(item, album),
                        executor
                ).exceptionally(ex -> {
                    log.warn("Video no disponible: {} - {}", item.getName(), ex.getMessage());
                    return null;
                }))
                .toList();
        List<Long> songIds = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .map(Song::getId)
                .toList();
        if (songIds.isEmpty()) {
            return List.of();
        }
        return songRepository.findAllByIdWithArtists(songIds);
    }

    private Song fetchWithRateLimit(StreamInfoItem item, Album album) {
        try {
            if (!NEWPIPE_SEMAPHORE.tryAcquire(30, TimeUnit.SECONDS)) {
                log.warn("Timeout esperando permiso para: {}", item.getName());
                return null;
            }
            try {
                Song result = songService.getOrCreateSongFromAlbum(item, album);
                Thread.sleep(DELAY_BETWEEN_REQUESTS_MS);
                return result;
            } finally {
                NEWPIPE_SEMAPHORE.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrumpido mientras esperaba: {}", item.getName());
            return null;
        }
    }
}
