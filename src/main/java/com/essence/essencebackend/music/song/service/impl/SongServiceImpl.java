package com.essence.essencebackend.music.song.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import com.essence.essencebackend.music.song.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Override
    public List<SongResponseSimpleDTO> getTrendingSongs(String username) {
        log.info("Obteniendo canciones mas escuchadas por el usuario: {}", username);

        List<Song> songs = songRepository.findTop20ByOrderByTotalStreamsDesc();

        if (songs.isEmpty()) {

        }

        return List.of();
    }
}
