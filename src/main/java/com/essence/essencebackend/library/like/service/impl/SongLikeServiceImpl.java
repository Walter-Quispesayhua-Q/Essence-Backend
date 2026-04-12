package com.essence.essencebackend.library.like.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.model.SongLike;
import com.essence.essencebackend.library.like.model.embedded.SongLikeId;
import com.essence.essencebackend.library.like.repository.SongLikeRepository;
import com.essence.essencebackend.library.like.service.BaseLikeService;
import com.essence.essencebackend.autentication.shared.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.model.PlaylistSong;
import com.essence.essencebackend.library.playlist.model.PlaylistType;
import com.essence.essencebackend.library.playlist.model.embedded.PlaylistSongId;
import com.essence.essencebackend.library.playlist.repository.PlaylistRepository;
import com.essence.essencebackend.library.playlist.repository.PlaylistSongRepository;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SongLikeServiceImpl extends BaseLikeService
        <Song, Long, SongLike, SongLikeId> {

    private final SongRepository songRepository;
    private final SongLikeRepository songLikeRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;

    public SongLikeServiceImpl(
            UserRepository userRepository,
            SongRepository songRepository,
            SongLikeRepository songLikeRepository,
            PlaylistRepository playlistRepository,
            PlaylistSongRepository playlistSongRepository
            ) {
        super(userRepository);
        this.songRepository = songRepository;
        this.songLikeRepository = songLikeRepository;
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
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

    @Override
    @Transactional
    public void addLike(Long id, String username) {
        User user = getUserByUsername(username);
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new SongNotFoundException(id));

        SongLikeId likeId = buildLikeId(song, user);
        if (songLikeRepository.existsById(likeId)) {
            return;
        }

        try {
            SongLike like = buildLikeEntity(likeId, song, user);
            songLikeRepository.save(like);
        } catch (DataIntegrityViolationException ignored) {
        }

        Playlist likedPlaylist = getOrCreateLikedPlaylist(user);
        PlaylistSongId psId = new PlaylistSongId(likedPlaylist.getPlaylistId(), song.getId());

        if (!playlistSongRepository.existsById(psId)) {
            try {
                PlaylistSong ps = new PlaylistSong();
                ps.setId(psId);
                ps.setPlaylist(likedPlaylist);
                ps.setSong(song);
                ps.setAddedAt(Instant.now());
                ps.setSongOrder(
                        (int) playlistSongRepository.countByPlaylistPlaylistId(likedPlaylist.getPlaylistId()) + 1
                );
                playlistSongRepository.save(ps);
            } catch (DataIntegrityViolationException ignored) {
            }
        }
    }

    @Override
    @Transactional
    public void deleteLike(Long id, String username) {
        super.deleteLike(id, username);

        User user = getUserByUsername(username);
        playlistRepository.findByUserAndType(user, PlaylistType.LIKED)
                .ifPresent(likedPlaylist -> {
                    PlaylistSongId psId = new PlaylistSongId(likedPlaylist.getPlaylistId(), id);
                    playlistSongRepository.deleteById(psId);
                });
    }

    private Playlist getOrCreateLikedPlaylist(User user) {
        return playlistRepository.findByUserAndType(user, PlaylistType.LIKED)
                .orElseGet(() -> {
                    Playlist liked = new Playlist();
                    liked.setTitle("Liked Songs");
                    liked.setDescription("Tus canciones favoritas");
                    liked.setUser(user);
                    liked.setType(PlaylistType.LIKED);
                    liked.setIsPublic(false);
                    try {
                        return playlistRepository.save(liked);
                    } catch (DataIntegrityViolationException ex) {
                        return playlistRepository.findByUserAndType(user, PlaylistType.LIKED)
                                .orElseThrow(() -> ex);
                    }
                });
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );
    }
}
