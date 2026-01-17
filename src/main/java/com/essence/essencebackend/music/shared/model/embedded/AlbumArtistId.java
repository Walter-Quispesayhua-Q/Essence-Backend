package com.essence.essencebackend.music.shared.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class AlbumArtistId implements Serializable {
    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "artist_id")
    private Long artistId;
}
