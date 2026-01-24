package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.like.model.SongLike;
import com.essence.essencebackend.library.like.model.embedded.SongLikeId;
import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongLikeRepository extends JpaRepository<SongLike, SongLikeId> {
}
