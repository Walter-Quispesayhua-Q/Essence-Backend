package com.essence.essencebackend.music.song.mapper;

import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.service.ImageResolver;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongSyncRequestDTO;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.model.SongStatus;
import lombok.RequiredArgsConstructor;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class SongMapperByInfo {

    private final UrlExtractor urlExtractor;
    private final ImageResolver imageResolver;

    public Song mapToSong(StreamInfo info, String streamingUrl, String streamingUrlId) {
        Song song = new Song();
        song.setTitle(info.getName());
        song.setDurationMs((int) (info.getDuration() * 1000));
        song.setHlsMasterKey(streamingUrlId);
        song.setStreamingUrl(streamingUrl);
        song.setImageKey(imageResolver.resolve(info.getThumbnails()));
        song.setTotalStreams(info.getViewCount() >= 0 ? info.getViewCount() : 0L);
        song.setLastSyncedAt(Instant.now());
        song.setStatus(SongStatus.ACTIVE);

        DateWrapper uploadDate = info.getUploadDate();
        if (uploadDate != null) {
            song.setReleaseDate(uploadDate.offsetDateTime().toLocalDate());
        }

        return song;
    }

    public SongResponseSimpleDTO mapFromItem(StreamInfoItem item) {
        long viewCount = item.getViewCount();
        DateWrapper uploadDate = item.getUploadDate();
        java.time.LocalDate releaseDate = uploadDate != null
                ? uploadDate.offsetDateTime().toLocalDate()
                : null;

        return new SongResponseSimpleDTO(
                null,
                item.getName(),
                (int) (item.getDuration() * 1000),
                urlExtractor.extractId(item.getUrl(), ContentType.SONG),
                imageResolver.resolve(item.getThumbnails()),
                "MUSIC",
                viewCount >= 0 ? viewCount : null,
                item.getUploaderName(),
                null,
                releaseDate
        );
    }

    /** Mapea desde datos del cliente */
    public Song mapFromClientSync(SongSyncRequestDTO request) {
        Song song = new Song();
        song.setTitle(request.title());
        song.setDurationMs(request.durationMs());
        song.setHlsMasterKey(request.videoId());
        song.setStreamingUrl(request.streamingUrl());
        song.setImageKey(request.thumbnailUrl());
        song.setTotalStreams(request.viewCount() != null ? request.viewCount() : 0L);
        song.setReleaseDate(request.releaseDate());
        song.setLastSyncedAt(Instant.now());
        song.setStatus(SongStatus.ACTIVE);
        return song;
    }


}
