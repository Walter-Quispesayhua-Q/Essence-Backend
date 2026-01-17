package com.essence.essencebackend.music.song.model;

import com.essence.essencebackend.music.album.model.Album;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "hls_master_key", nullable = false)
    private String hlsMasterKey;

    @Column(name = "cover_key")
    private String coverKey;

    @Column(name = "isrc", unique = true)
    @Size(max = 12)
    private char isrc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album albumId;

    @Column(name = "status", nullable = false)
    @Size(max = 20)
    private String status = "PROCESSING";

    @Column(name = "total_plays")
    private Long totalPlays = 0L;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SongArtist> songArtists;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
