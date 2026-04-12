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
public class PlaylistLikeId implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "playlist_id", nullable = false)
    private Long playlistId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

}
