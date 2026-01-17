package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.library.like.model.SongLike;
import com.essence.essencebackend.library.like.model.embedded.SongLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongLikeRepository extends JpaRepository<SongLike, SongLikeId> {
}
