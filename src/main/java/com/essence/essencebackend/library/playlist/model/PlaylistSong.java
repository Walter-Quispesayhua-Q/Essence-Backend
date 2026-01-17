package com.essence.essencebackend.library.playlist.model;

import com.essence.essencebackend.music.song.model.embedded.PlaylistSongId;
import com.essence.essencebackend.music.song.model.Song;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "playlist_songs")
public class PlaylistSong {
    @EmbeddedId
    private PlaylistSongId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("songId")
    @JoinColumn(name = "song_id")
    private Song song;

    @Column(name = "added_at")
    private Instant addedAt = Instant.now();

    @Column(name = "song_order")
    private Integer songOrder = 1;
}
