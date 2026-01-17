package com.essence.essencebackend.library.history.model;

import com.essence.essencebackend.music.song.model.Song;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playback_segments")
public class PlaybackSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "segment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(name = "segment_start_ms", nullable = false)
    private Integer segmentStartMs;

    @Column(name = "segment_end_ms", nullable = false)
    private Integer segmentEndMs;

    @Column(name = "play_count")
    private Long playCount = 0L;
}