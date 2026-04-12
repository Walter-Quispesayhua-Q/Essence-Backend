package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.library.like.model.PlaylistLike;
import com.essence.essencebackend.library.like.model.embedded.PlaylistLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, PlaylistLikeId> {
    boolean existsById_PlaylistIdAndId_UserId(Long playlistId, Long userId);
}
