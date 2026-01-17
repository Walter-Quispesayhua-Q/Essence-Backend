package com.essence.essencebackend.library.like.model;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.like.model.embedded.SongLikeId;
import com.essence.essencebackend.music.song.model.Song;
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
@Table(name = "song_likes")
public class SongLike {

    @EmbeddedId
    private SongLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("songId")
    @JoinColumn(name = "song_id")
    private Song song;

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
