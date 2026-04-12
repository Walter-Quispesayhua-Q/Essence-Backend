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
public class AlbumLikeId implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
