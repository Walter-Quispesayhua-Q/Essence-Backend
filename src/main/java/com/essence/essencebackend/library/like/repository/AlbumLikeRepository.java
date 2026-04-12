package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.library.like.model.AlbumLike;
import com.essence.essencebackend.library.like.model.embedded.AlbumLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumLikeRepository extends JpaRepository<AlbumLike, AlbumLikeId> {
    boolean existsById_AlbumIdAndId_UserId(Long albumId, Long userId);

    @Query("SELECT CASE WHEN COUNT(al) > 0 THEN true ELSE false END FROM AlbumLike al WHERE al.id.albumId = :albumId AND al.user.username = :username")
    boolean existsByAlbumIdAndUsername(@Param("albumId") Long albumId, @Param("username") String username);
}
