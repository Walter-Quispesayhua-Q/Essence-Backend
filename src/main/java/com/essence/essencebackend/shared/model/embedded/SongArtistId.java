package com.essence.essencebackend.shared.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
public class SongArtistId implements Serializable {
    @Column(name = "song_id")
    private Long songId;

    @Column(name = "artist_id")
    private Long artistId;
}
