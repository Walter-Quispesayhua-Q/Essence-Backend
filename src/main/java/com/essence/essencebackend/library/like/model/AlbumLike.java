package com.essence.essencebackend.library.like.model;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.like.model.embedded.AlbumLikeId;
import com.essence.essencebackend.music.album.model.Album;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "album_likes")
public class AlbumLike {

    @EmbeddedId
    private AlbumLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("albumId")
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "liked_at")
    private Instant likedAt;

    @PrePersist
    public void onCreate() {
        this.likedAt = Instant.now();
    }
}
