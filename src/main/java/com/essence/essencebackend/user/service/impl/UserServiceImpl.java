package com.essence.essencebackend.user.service.impl;

import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.autentication.shared.exception.UserNotFoundForUsernameException;
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
                .orElseThrow(() -> new UserNotFoundForUsernameException(username));

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
                .orElseThrow(() -> new UserNotFoundForUsernameException(username));

        Object[] stats = userRepository.countUserStats(user.getId());
        Object[] row = (Object[]) stats[0];

        return new UserDetailDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                ((Number) row[0]).intValue(),
                ((Number) row[1]).intValue(),
                ((Number) row[2]).intValue(),
                ((Number) row[3]).intValue(),
                ((Number) row[4]).intValue()
        );
    }
}
