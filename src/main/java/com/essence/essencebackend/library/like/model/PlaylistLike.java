package com.essence.essencebackend.library.like.model;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.like.model.embedded.PlaylistLikeId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlist_likes")
public class PlaylistLike {

    @EmbeddedId
    private PlaylistLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "liked_at")
    private Instant likedAt = Instant.now();
}
