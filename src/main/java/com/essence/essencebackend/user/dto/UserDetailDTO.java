package com.essence.essencebackend.user.dto;

import java.time.Instant;

public record UserDetailDTO(
        Long id,
        String username,
        String email,
        Instant createdAt,
        Instant updatedAt,
        Integer totalLikedSongs,
        Integer totalLikedAlbums,
        Integer totalLikedArtists,
        Integer totalPlaylists,
        Integer totalPlayHistory
) {
}
