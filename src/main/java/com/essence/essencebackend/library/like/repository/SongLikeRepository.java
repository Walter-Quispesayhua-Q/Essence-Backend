package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.like.model.SongLike;
import com.essence.essencebackend.library.like.model.embedded.SongLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongLikeRepository extends JpaRepository<SongLike, SongLikeId> {
    boolean existsById_SongIdAndId_UserId(Long songId, Long userId);

    @Query("SELECT CASE WHEN COUNT(sl) > 0 THEN true ELSE false END FROM SongLike sl WHERE sl.id.songId = :songId AND sl.user.username = :username")
    boolean existsBySongIdAndUsername(@Param("songId") Long songId, @Param("username") String username);
}
