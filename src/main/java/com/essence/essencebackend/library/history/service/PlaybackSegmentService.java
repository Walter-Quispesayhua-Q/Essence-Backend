package com.essence.essencebackend.library.history.service;

import com.essence.essencebackend.music.song.model.Song;

public interface PlaybackSegmentService {
    void createSegmentsForSong(Song song);

    void updateSegmentsCounts(
            Song song, int durationListenedMs,
            boolean skipped, Integer skipPositionMs);
}
