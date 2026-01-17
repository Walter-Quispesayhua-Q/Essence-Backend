package com.essence.essencebackend.music.song.model;

import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.shared.model.embedded.SongArtistId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "song_artists")
public class SongArtist {

    @EmbeddedId
    private SongArtistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("songId")
    @JoinColumn(name = "song_id")
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "artist_order")
    private Integer artistOrder = 1;

}
