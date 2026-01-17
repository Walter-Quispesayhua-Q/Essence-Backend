package com.essence.essencebackend.library.like.model.embedded;

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
public class ArtistLikeId implements Serializable {
    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
