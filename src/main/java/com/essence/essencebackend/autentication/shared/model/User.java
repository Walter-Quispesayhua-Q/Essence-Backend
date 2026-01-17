package com.essence.essencebackend.autentication.shared.model;

import com.essence.essencebackend.library.history.model.PlayHistory;
import com.essence.essencebackend.library.like.model.AlbumLike;
import com.essence.essencebackend.library.like.model.ArtistLike;
import com.essence.essencebackend.library.like.model.PlaylistLike;
import com.essence.essencebackend.library.like.model.SongLike;
import com.essence.essencebackend.library.playlist.model.Playlist;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "enabled")
    private boolean enabled = true;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<SongLike> songLikes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AlbumLike> albumLikes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ArtistLike> artistLikes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PlaylistLike> playlistLikes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Playlist> playlists;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PlayHistory> playHistory;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
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
