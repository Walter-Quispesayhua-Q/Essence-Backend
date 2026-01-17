package com.essence.essencebackend.library.like.model;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.like.model.embedded.ArtistLikeId;
import com.essence.essencebackend.music.artist.model.Artist;
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
@Table(name = "artist_likes")
public class ArtistLike {

    @EmbeddedId
    private ArtistLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

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
