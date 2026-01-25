package com.essence.essencebackend.library.like.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.model.PlaylistLike;
import com.essence.essencebackend.library.like.model.embedded.PlaylistLikeId;
import com.essence.essencebackend.library.like.repository.PlaylistLikeRepository;
import com.essence.essencebackend.library.like.service.BaseLikeService;
import com.essence.essencebackend.library.playlist.exception.PlaylistNotFoundException;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.repository.PlaylistRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PlaylistLikeServiceImpl extends BaseLikeService
    <Playlist, Long, PlaylistLike, PlaylistLikeId> {

    private final PlaylistLikeRepository playlistLikeRepository;
    private final PlaylistRepository playlistRepository;

    public PlaylistLikeServiceImpl(
            UserRepository userRepository,
            PlaylistRepository playlistRepository,
            PlaylistLikeRepository playlistLikeRepository
    ) {
        super(userRepository);
        this.playlistLikeRepository = playlistLikeRepository;
        this.playlistRepository = playlistRepository;
    }

    @Override
    protected JpaRepository<Playlist, Long> entityRepository() {
        return playlistRepository;
    }

    @Override
    protected JpaRepository<PlaylistLike, PlaylistLikeId> likeRepository() {
        return playlistLikeRepository;
    }

    @Override
    protected RuntimeException entityNotFound(Long playlistId) {
        return new PlaylistNotFoundException(playlistId);
    }

    @Override
    protected PlaylistLikeId buildLikeId(Playlist entity, User user) {
        return new PlaylistLikeId(entity.getPlaylistId(), user.getId());
    }

    @Override
    protected PlaylistLike buildLikeEntity(PlaylistLikeId playlistLikeId, Playlist entity, User user) {
        PlaylistLike playlistLike = new PlaylistLike();
        playlistLike.setId(playlistLikeId);
        playlistLike.setUser(user);
        playlistLike.setPlaylist(entity);
        return playlistLike;
    }
}
