package com.essence.essencebackend.music.artist.model;

import com.essence.essencebackend.music.album.model.AlbumArtist;
import com.essence.essencebackend.music.song.model.SongArtist;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Long id;

    @Column(name = "name_artist", nullable = false)
    private String nameArtist;

    @Column(name = "description")
    private String description;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "artist_url")
    private String artistUrl;

    @Column(name = "country", length = 10)
    @Size(max = 10)
    private String country;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AlbumArtist> albumArtists;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
