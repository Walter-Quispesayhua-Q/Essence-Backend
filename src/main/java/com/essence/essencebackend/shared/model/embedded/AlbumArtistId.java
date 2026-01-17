package com.essence.essencebackend.shared.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
public class AlbumArtistId implements Serializable {
    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "artist_id")
    private Long artistId;
}
