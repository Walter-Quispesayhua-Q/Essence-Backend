package com.essence.essencebackend.library.like.repository;

import com.essence.essencebackend.library.like.model.ArtistLike;
import com.essence.essencebackend.library.like.model.embedded.ArtistLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistLikeRepository extends JpaRepository<ArtistLike, ArtistLikeId> {
    boolean existsById_ArtistIdAndId_UserId(Long artistId, Long userId);

    @Query("SELECT CASE WHEN COUNT(al) > 0 THEN true ELSE false END FROM ArtistLike al WHERE al.id.artistId = :artistId AND al.user.username = :username")
    boolean existsByArtistIdAndUsername(@Param("artistId") Long artistId, @Param("username") String username);
}
