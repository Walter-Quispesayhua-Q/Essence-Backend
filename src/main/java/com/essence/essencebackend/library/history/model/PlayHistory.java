package com.essence.essencebackend.library.history.model;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.song.model.Song;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "play_history")
public class PlayHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "played_at")
    private Instant playedAt;

    @Column(name = "duration_listened_ms")
    private Integer durationListenedMs = 0;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "skipped")
    private Boolean skipped = false;

    @Column(name = "skip_position_ms")
    private Integer skipPositionMs;

    @Column(name = "device_type")
    private String deviceType;

    @PrePersist
    public void onCreate() {
        this.playedAt = Instant.now();
    }
}