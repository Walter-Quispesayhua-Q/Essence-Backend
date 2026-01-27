package com.essence.essencebackend.library.history.service.impl;

import com.essence.essencebackend.library.history.model.PlaybackSegment;
import com.essence.essencebackend.library.history.repository.PlaybackSegmentRepository;
import com.essence.essencebackend.library.history.service.PlaybackSegmentService;
import com.essence.essencebackend.music.song.model.Song;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlaybackSegmentServiceImpl implements PlaybackSegmentService {

    private final PlaybackSegmentRepository playbackSegmentRepository;

    private static final int SEGMENT_DURATION_MS = 10000; // 10 segundos

    @Override
    @Transactional
    public void createSegmentsForSong(Song song) {
        int durationMs = song.getDurationMs();
        int segmentNumber = 1;

        for (int start = 0; start < durationMs; start += SEGMENT_DURATION_MS) {
            int end = Math.min(start + SEGMENT_DURATION_MS, durationMs);

            PlaybackSegment segment = new PlaybackSegment();
            segment.setSong(song);
            segment.setSegmentNumber(segmentNumber++);
            segment.setSegmentStartMs(start);
            segment.setSegmentEndMs(end);
            segment.setPlayCount(0L);
            segment.setCompleteCount(0L);
            segment.setSkipCount(0L);

            playbackSegmentRepository.save(segment);
        }
    }

    @Override
    @Transactional
    public void updateSegmentsCounts(Song song, int durationListenedMs, boolean skipped, Integer skipPositionMs) {
        List<PlaybackSegment> segments = playbackSegmentRepository.findBySongOrderBySegmentNumber((song));

        for (PlaybackSegment segment : segments) {
            // Si escuchó hasta este segmento
            if (durationListenedMs >= segment.getSegmentStartMs()) {
                segment.setPlayCount(segment.getPlayCount() + 1);

                // Si completó este segmento
                if (durationListenedMs >= segment.getSegmentEndMs()) {
                    segment.setCompleteCount(segment.getCompleteCount() + 1);
                }

                // Si saltó EN este segmento
                if (skipped && skipPositionMs != null
                        && skipPositionMs >= segment.getSegmentStartMs()
                        && skipPositionMs < segment.getSegmentEndMs()) {
                    segment.setSkipCount(segment.getSkipCount() + 1);
                }

                playbackSegmentRepository.save(segment);
            } else {
                break;
            }
        }
    }
}
