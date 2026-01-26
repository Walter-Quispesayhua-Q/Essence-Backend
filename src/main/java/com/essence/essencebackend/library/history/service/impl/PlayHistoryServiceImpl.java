package com.essence.essencebackend.library.history.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.history.dto.PlayHistoryRequestDTO;
import com.essence.essencebackend.library.history.model.PlayHistory;
import com.essence.essencebackend.library.history.repository.PlayHistoryRepository;
import com.essence.essencebackend.library.history.service.PlayHistoryService;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlayHistoryServiceImpl implements PlayHistoryService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlayHistoryRepository playHistoryRepository;

    @Override
    @Transactional
    public void addSongToHistory(
            Long id, String username, PlayHistoryRequestDTO data) {
        log.info("Agregando canción: {}, al historial del usuario: {}", id, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundForUsernameException(username)
                );
        Song song = songRepository.findById(id).orElseThrow(
                () -> new SongNotFoundException(id)
        );

        PlayHistory playHistoryExists = playHistoryRepository.findByUserIdAndSongId(user.getId(), song.getId());

        if (playHistoryExists != null) {
            return;
        }
    }

}
