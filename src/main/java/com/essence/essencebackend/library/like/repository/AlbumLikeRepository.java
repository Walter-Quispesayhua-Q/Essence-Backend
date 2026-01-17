package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.library.like.model.AlbumLike;
import com.essence.essencebackend.library.like.model.embedded.AlbumLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumLikeRepository extends JpaRepository<AlbumLike, AlbumLikeId> {
}
