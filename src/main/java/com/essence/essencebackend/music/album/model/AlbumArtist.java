package com.essence.essencebackend.music.album.model;

import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.shared.model.embedded.AlbumArtistId;
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
@Table(name = "album_artists")
public class AlbumArtist {

    @EmbeddedId
    private AlbumArtistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("albumId")
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "artist_order")
    private Integer artistOrder = 1;
}
