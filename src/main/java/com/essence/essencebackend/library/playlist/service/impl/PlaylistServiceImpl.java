package com.essence.essencebackend.library.playlist.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;
import com.essence.essencebackend.library.playlist.exception.*;
import com.essence.essencebackend.library.playlist.mapper.PlaylistMapper;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.repository.PlaylistRepository;
import com.essence.essencebackend.library.playlist.service.PlaylistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final UserRepository userRepository;
    private final PlaylistMapper playlistMapper;
    private final PlaylistRepository playlistRepository;

    @Transactional
    @Override
    public PlaylistSimpleResponseDTO createPlaylist(PlaylistRequestDTO data, String username) {
        log.info("Iniciando la creación de una nueva playlist con los datos:{} ", data);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        if (data.title().isBlank()) {
            throw new TitleEmptyException();
        }

        if (playlistRepository.existsByTitleAndUser(data.title(), user)) {
            throw new DuplicatePlaylistException(data.title());
        }

        Playlist playlist = playlistMapper.toEntity(data);
        playlist.setUser(user);
        playlistRepository.save(playlist);

        return playlistMapper.toDtoSimple(playlist);
    }

    @Override
    @Transactional
    public PlaylistSimpleResponseDTO updatePlaylist(Long id, PlaylistRequestDTO dataUpdate, String username) {
        log.info("Actualizando Playlist con el id: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        if (dataUpdate.title() != null && dataUpdate.title().isBlank()) {
            throw new TitleEmptyException();
        }

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );

        Playlist playlistUpdate = playlistMapper.toUpdateEntity(dataUpdate, playlist);

        playlistRepository.save(playlistUpdate);
        return playlistMapper.toDtoSimple(playlistUpdate);
    }

    @Override
    public PlaylistResponseDTO getPlaylist(Long id, String username) {
        log.info("Obteniendo playlist por el id: {} , para el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );

        return playlistMapper.toDto(playlist);
    }

    @Override
    public PlaylistSimpleResponseDTO getForUpdate(Long id, String username) {
        log.info("Obteniendo playlist para editar con el id: {} , para el usuario: {}", id, username);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );
        return playlistMapper.toDtoSimple(playlist);
    }


    @Override
    @Transactional
    public boolean deletePlaylist(Long id, String username) {
        log.info("Procediendo con la eliminación del playlist con la id: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );

        try {
            playlistRepository.delete(playlist);
            return true;
        } catch (RuntimeException e) {
            throw new FailedToDeletePlaylistException(playlist.getPlaylistId());
        }
    }
}
