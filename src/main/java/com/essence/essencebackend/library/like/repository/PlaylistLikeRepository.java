package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.library.like.model.PlaylistLike;
import com.essence.essencebackend.library.like.model.embedded.PlaylistLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, PlaylistLikeId> {
}
