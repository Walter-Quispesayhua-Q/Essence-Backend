package com.essence.essencebackend.library.history.repository;

import com.essence.essencebackend.library.history.model.PlaybackSegment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackSegmentRepository extends JpaRepository<PlaybackSegment, Long> {
}
