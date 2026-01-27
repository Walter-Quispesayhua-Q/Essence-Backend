package com.essence.essencebackend.library.history.repository;

import com.essence.essencebackend.library.history.model.PlaybackSegment;
import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaybackSegmentRepository extends JpaRepository<PlaybackSegment, Long> {
    List<PlaybackSegment> findBySongOrderBySegmentNumber(Song song);
}
