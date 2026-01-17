package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.library.like.model.ArtistLike;
import com.essence.essencebackend.library.like.model.embedded.ArtistLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistLikeRepository extends JpaRepository<ArtistLike, ArtistLikeId> {
}
