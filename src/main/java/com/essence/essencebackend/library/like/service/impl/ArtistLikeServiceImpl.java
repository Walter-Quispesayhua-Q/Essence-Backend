package com.essence.essencebackend.library.like.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.model.ArtistLike;
import com.essence.essencebackend.library.like.model.embedded.ArtistLikeId;
import com.essence.essencebackend.library.like.repository.ArtistLikeRepository;
import com.essence.essencebackend.library.like.service.BaseLikeService;
import com.essence.essencebackend.music.artist.exception.ArtistNotFoundException;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ArtistLikeServiceImpl extends BaseLikeService
    <Artist, Long, ArtistLike, ArtistLikeId> {

    private final ArtistLikeRepository artistLikeRepository;
    private final ArtistRepository artistRepository;

    public ArtistLikeServiceImpl(
            UserRepository userRepository,
            ArtistRepository artistRepository,
            ArtistLikeRepository artistLikeRepository
    ) {
        super(userRepository);
        this.artistLikeRepository = artistLikeRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    protected JpaRepository<Artist, Long> entityRepository() {
        return artistRepository;
    }

    @Override
    protected JpaRepository<ArtistLike, ArtistLikeId> likeRepository() {
        return artistLikeRepository;
    }

    @Override
    protected RuntimeException entityNotFound(Long artistId) {
        return new ArtistNotFoundException(artistId);
    }

    @Override
    protected ArtistLikeId buildLikeId(Artist entity, User user) {
        return new ArtistLikeId(entity.getId(), user.getId());
    }

    @Override
    protected ArtistLike buildLikeEntity(ArtistLikeId artistLikeId, Artist entity, User user) {
        ArtistLike artistLike = new ArtistLike();
        artistLike.setId(artistLikeId);
        artistLike.setArtist(entity);
        artistLike.setUser(user);
        return artistLike;
    }
}
