package com.essence.essencebackend.library.like.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.model.SongLike;
import com.essence.essencebackend.library.like.model.embedded.SongLikeId;
import com.essence.essencebackend.library.like.repository.SongLikeRepository;
import com.essence.essencebackend.library.like.service.BaseLikeService;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SongLikeServiceImpl extends BaseLikeService
        <Song, Long, SongLike, SongLikeId> {

    private final SongRepository songRepository;
    private final SongLikeRepository songLikeRepository;

    public SongLikeServiceImpl(
            UserRepository userRepository,
            SongRepository songRepository,
            SongLikeRepository songLikeRepository
            ) {
        super(userRepository);
        this.songRepository = songRepository;
        this.songLikeRepository = songLikeRepository;
    }

    @Override
    protected JpaRepository<Song, Long> entityRepository() {
        return songRepository;
    }

    @Override
    protected JpaRepository<SongLike, SongLikeId> likeRepository() {
        return songLikeRepository;
    }

    @Override
    protected RuntimeException entityNotFound(Long songId) {
        return new SongNotFoundException(songId);
    }

    @Override
    protected SongLikeId buildLikeId(Song entity, User user) {
        return new SongLikeId(entity.getId(), user.getId());
    }

    @Override
    protected SongLike buildLikeEntity(SongLikeId songLikeId, Song entity, User user) {
        SongLike songLike = new SongLike();
        songLike.setId(songLikeId);
        songLike.setSong(entity);
        songLike.setUser(user);
        return songLike;
    }
}
