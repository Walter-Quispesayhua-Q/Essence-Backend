package com.essence.essencebackend.library.like.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.model.AlbumLike;
import com.essence.essencebackend.library.like.model.embedded.AlbumLikeId;
import com.essence.essencebackend.library.like.repository.AlbumLikeRepository;
import com.essence.essencebackend.library.like.service.BaseLikeService;
import com.essence.essencebackend.music.album.exception.AlbumNotFoundException;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.repository.AlbumRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AlbumLikeServiceImpl extends BaseLikeService
<Album, Long, AlbumLike, AlbumLikeId> {

    private final AlbumRepository albumRepository;
    private final AlbumLikeRepository albumLikeRepository;

    public AlbumLikeServiceImpl(UserRepository userRepository, AlbumLikeRepository albumLikeRepository
    , AlbumRepository albumRepository) {
        super(userRepository);
        this.albumLikeRepository = albumLikeRepository;
        this.albumRepository = albumRepository;
    }

    @Override
    protected JpaRepository<Album, Long> entityRepository() {
        return albumRepository;
    }

    @Override
    protected JpaRepository<AlbumLike, AlbumLikeId> likeRepository() {
        return albumLikeRepository;
    }

    @Override
    protected RuntimeException entityNotFound(Long albumId) {
        return new AlbumNotFoundException(albumId);
    }

    @Override
    protected AlbumLikeId buildLikeId(Album entity, User user) {
        return new AlbumLikeId(entity.getId(), user.getId());
    }

    @Override
    protected AlbumLike buildLikeEntity(AlbumLikeId albumLikeId, Album entity, User user) {
        AlbumLike albumLike = new AlbumLike();
        albumLike.setId(albumLikeId);
        albumLike.setAlbum(entity);
        albumLike.setUser(user);
        return albumLike;
    }
}
