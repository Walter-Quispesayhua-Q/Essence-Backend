package com.essence.essencebackend.library.like.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.model.SongLike;
import com.essence.essencebackend.library.like.model.embedded.SongLikeId;
import com.essence.essencebackend.library.like.repository.SongLikeRepository;
import com.essence.essencebackend.library.like.service.LikeService;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    private final UserRepository userRepository;
    private final SongLikeRepository songLikeRepository;
    private final SongRepository songRepository;

    private <T, ID> T findByIdOrThrow(JpaRepository<T, ID> getRepository, ID id) {
        getRepository.findById(id).orElseThrow(
                () -> new RuntimeException("")
        );

    }

    @Override
    @Transactional
    public boolean addLikeToSong(Long id, String username) {
        log.info("Agregándole me gusta a la canción con el: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Song song = songRepository.findById(id).orElseThrow(
                () -> new SongNotFoundException(id)
        );

        SongLikeId songLikeId = new SongLikeId(song.getId(), user.getId());

        if (songLikeRepository.existsById(songLikeId)) {
            return false;
        }

        SongLike songLike = new SongLike();
        songLike.setId(songLikeId);
        songLike.setSong(song);
        songLike.setUser(user);

        songLikeRepository.save(songLike);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteLikeToSong(Long id, String username) {
        log.info("Quitando me gusta a la canción con el: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Song song = songRepository.findById(id).orElseThrow(
                () -> new SongNotFoundException(id)
        );

        SongLikeId songLikeId = new SongLikeId(song.getId(), user.getId());

        if (!songLikeRepository.existsById(songLikeId)) {
            return false;
        }

        songLikeRepository.deleteById(songLikeId);

        return true;
    }
}
