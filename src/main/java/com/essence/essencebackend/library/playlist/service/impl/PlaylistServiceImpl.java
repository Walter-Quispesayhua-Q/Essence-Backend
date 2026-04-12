package com.essence.essencebackend.library.playlist.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.repository.PlaylistLikeRepository;
import com.essence.essencebackend.library.playlist.dto.PlaylistCreateRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistEditResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistListResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistUpdateRequestDTO;
import com.essence.essencebackend.library.playlist.exception.*;
import com.essence.essencebackend.autentication.shared.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.library.playlist.mapper.PlaylistMapper;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.model.PlaylistType;
import com.essence.essencebackend.library.playlist.repository.PlaylistRepository;
import com.essence.essencebackend.library.playlist.service.PlaylistService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final UserRepository userRepository;
    private final PlaylistMapper playlistMapper;
    private final PlaylistRepository playlistRepository;
    private final PlaylistLikeRepository playlistLikeRepository;

    @Transactional
    @Override
    public PlaylistSimpleResponseDTO createPlaylist(PlaylistCreateRequestDTO data, String username) {
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

        if (data.title().equalsIgnoreCase("Liked Songs")) {
            throw new DuplicatePlaylistException("Liked Songs");
        }

        Playlist playlist = playlistMapper.toEntity(data);
        playlist.setUser(user);
        playlistRepository.save(playlist);

        return playlistMapper.toDtoSimple(playlist);
    }

    @Override
    @Transactional
    public PlaylistSimpleResponseDTO updatePlaylist(Long id, PlaylistUpdateRequestDTO dataUpdate, String username) {
        log.info("Actualizando Playlist con el UrlId: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        if (dataUpdate.title() != null && dataUpdate.title().isBlank()) {
            throw new TitleEmptyException();
        }

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );

        if (playlist.getType() == PlaylistType.LIKED) {
            throw new SystemPlaylistCannotBeModifiedException();
        }

        Playlist playlistUpdate = playlistMapper.toUpdateEntity(dataUpdate, playlist);

        playlistRepository.save(playlistUpdate);
        return playlistMapper.toDtoSimple(playlistUpdate);
    }

    @Override
    public PlaylistResponseDTO getPlaylist(Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );
        Playlist playlist = playlistRepository.findAccessiblePlaylist(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );
        PlaylistResponseDTO dto = playlistMapper.toDto(playlist);
        boolean isLiked = playlistLikeRepository.existsById_PlaylistIdAndId_UserId(
                playlist.getPlaylistId(), user.getId()
        );
        return new PlaylistResponseDTO(
                dto.id(), dto.title(), dto.description(), dto.imageKey(),
                dto.isPublic(), dto.createdAt(), dto.updatedAt(),
                dto.totalSongs(), dto.type(), dto.totalLikes(),
                isLiked
        );
    }

    @Override
    public PlaylistEditResponseDTO getForUpdate(Long id, String username) {
        log.info("Obteniendo playlist para editar con el UrlId: {} , para el usuario: {}", id, username);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );
        return playlistMapper.toEditDto(playlist);
    }


    @Override
    @Transactional
    public void deletePlaylist(Long id, String username) {
        log.info("Procediendo con la eliminación del playlist con la UrlId: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );

        if (playlist.getType() == PlaylistType.LIKED) {
            throw new SystemPlaylistCannotBeDeletedException();
        }

        try {
            playlistRepository.delete(playlist);
        } catch (RuntimeException e) {
            throw new FailedToDeletePlaylistException(playlist.getPlaylistId());
        }
    }

    @Override
    public PlaylistListResponseDTO getAllPlaylists(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        List<PlaylistSimpleResponseDTO> myPlaylists = playlistRepository.findAllByUser(user)
                .stream()
                .filter(p -> {
                    if (p.getType() == PlaylistType.LIKED) {
                        return p.getPlaylistSongs() != null && !p.getPlaylistSongs().isEmpty();
                    }
                    return true;
                })
                .map(playlistMapper::toDtoSimple)
                .toList();

        List<PlaylistSimpleResponseDTO> publicPlaylists = playlistRepository
                .findAllByIsPublicTrueAndUserNot(user)
                .stream()
                .map(playlistMapper::toDtoSimple)
                .toList();

        return new PlaylistListResponseDTO(myPlaylists, publicPlaylists);
    }
}
