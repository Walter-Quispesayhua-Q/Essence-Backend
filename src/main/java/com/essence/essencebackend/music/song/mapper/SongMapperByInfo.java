package com.essence.essencebackend.music.song.mapper;

import com.essence.essencebackend.music.shared.service.UrlExtractor;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.model.Song;
import lombok.RequiredArgsConstructor;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class SongMapperByInfo {

    private final UrlExtractor urlExtractor;

    public Song mapToSong(StreamInfo info, String streamingUrl, String streamingUrlId) {
        Song song = new Song();
        song.setTitle(info.getName());
        song.setDurationMs((int) info.getDuration() * 1000);
        song.setHlsMasterKey(streamingUrlId);
        song.setStreamingUrl(streamingUrl);
        song.setImageKey(info.getThumbnails().isEmpty() ? null
                : info.getThumbnails().get(0).getUrl());
        song.setTotalStreams(info.getViewCount());
        song.setLastSyncedAt(Instant.now());
        song.setStatus("ACTIVE");
        return song;
    }

    public SongResponseSimpleDTO mapFromItem(StreamInfoItem item) {
        return new SongResponseSimpleDTO(
                null,
                item.getName(),
                (int) item.getDuration() * 1000,
                urlExtractor.extractId(item.getUrl(), UrlExtractor.ContentType.SONG),
                item.getThumbnails().isEmpty() ? null : item.getThumbnails().get(0).getUrl(),
                null,
                item.getViewCount(),
                item.getUploaderName(),
                null,
                null
        );
    }



}
