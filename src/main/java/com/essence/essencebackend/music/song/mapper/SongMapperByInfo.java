package com.essence.essencebackend.music.song.mapper;

import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
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

    public Song mapToSong(StreamInfo info, String streamingUrl, String streamingUrlId) {
        Song song = new Song();
        song.setTitle(info.getName());
        song.setDurationMs((int) (info.getDuration() * 1000));
        song.setHlsMasterKey(streamingUrlId);
        song.setStreamingUrl(streamingUrl);
        song.setImageKey(info.getThumbnails().stream()
                .max(java.util.Comparator.comparing(org.schabi.newpipe.extractor.Image::getHeight))
                .map(org.schabi.newpipe.extractor.Image::getUrl)
                .orElse(null));
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
                item.getThumbnails().stream()
                        .max(java.util.Comparator.comparing(org.schabi.newpipe.extractor.Image::getHeight))
                        .map(org.schabi.newpipe.extractor.Image::getUrl)
                        .orElse(null),
                "MUSIC",
                viewCount >= 0 ? viewCount : null,
                item.getUploaderName(),
                null,
                releaseDate
        );
    }



}
