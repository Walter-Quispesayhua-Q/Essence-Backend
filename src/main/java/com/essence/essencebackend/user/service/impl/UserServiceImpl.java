package com.essence.essencebackend.user.service.impl;

import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.user.dto.UserDetailDTO;
import com.essence.essencebackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public LoginResponseDTO getCurrentUser(String username) {
        log.info("Obteniendo usuario actual: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Override
    public UserDetailDTO getUserProfile(String username) {
        log.info("Obteniendo perfil del usuario: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new UserDetailDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getSongLikes() != null ? user.getSongLikes().size() : 0,
                user.getAlbumLikes() != null ? user.getAlbumLikes().size() : 0,
                user.getArtistLikes() != null ? user.getArtistLikes().size() : 0,
                user.getPlaylists() != null ? user.getPlaylists().size() : 0,
                user.getPlayHistory() != null ? user.getPlayHistory().size() : 0
        );
    }
}
