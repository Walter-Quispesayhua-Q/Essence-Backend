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
public class SongArtistId implements Serializable {
    @Column(name = "song_id")
    private Long songId;

    @Column(name = "artist_id")
    private Long artistId;
}
