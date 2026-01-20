package com.essence.essencebackend.library.playlist.service.impl;

import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;
import com.essence.essencebackend.library.playlist.exception.TitleEmptyException;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
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

        userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        if (data.title().isBlank() && data.title().isEmpty()) {
            throw new TitleEmptyException();
        }

        Playlist playlist = playlistMapper.toEntity(data);
        playlistRepository.save(playlist);

        return PlaylistSimpleResponseDTO.builder()
                .id(playlist.getPlaylistId())
                .title(playlist.getTitle())
                .isPublic(playlist.getIsPublic())
                .totalLikes(playlist.getIsPublic() ? playlist.getTotalLikes() : null)
                .build();
    }
}
