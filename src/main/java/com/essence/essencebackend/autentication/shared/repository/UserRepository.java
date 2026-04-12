package com.essence.essencebackend.autentication.shared.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    //== register
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    //==

    //== login
    Optional<User> findByEmail(String email);
    //==

    //== logica
    Optional<User> findByUsername(String username);

    @Query("""
        SELECT 
            (SELECT COUNT(sl) FROM SongLike sl WHERE sl.user.id = :userId),
            (SELECT COUNT(al) FROM AlbumLike al WHERE al.user.id = :userId),
            (SELECT COUNT(arl) FROM ArtistLike arl WHERE arl.user.id = :userId),
            (SELECT COUNT(p) FROM Playlist p WHERE p.user.id = :userId),
            (SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.user.id = :userId)
    """)
    Object[] countUserStats(@Param("userId") Long userId);

}
