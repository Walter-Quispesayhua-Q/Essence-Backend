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

@Slf4j
@Service
public class SongBatchServiceImpl implements SongBatchService {

    private final SongService songService;
    private final SongRepository songRepository;

    private final ExecutorService executor;

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
                        () -> songService.getOrCreateSongFromAlbum(item, album),
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
}
