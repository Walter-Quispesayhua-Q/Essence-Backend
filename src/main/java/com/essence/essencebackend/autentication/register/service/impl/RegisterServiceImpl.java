package com.essence.essencebackend.autentication.register.service.impl;

import com.essence.essencebackend.autentication.register.dto.RegisterRequestDTO;
import com.essence.essencebackend.autentication.register.dto.RegisterResponseDTO;
import com.essence.essencebackend.autentication.register.exception.DuplicateEmailException;
import com.essence.essencebackend.autentication.register.exception.DuplicateUsernameException;
import com.essence.essencebackend.autentication.shared.mapper.UserMapper;
import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.autentication.register.service.RegisterService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean getAvailableUsername(String username) {
        log.info("Consulta de disponibilidad de username: {}", username);
        return !userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public RegisterResponseDTO createUser(RegisterRequestDTO data) {
        log.info("Iniciando creacion de usuario. username={}", data.username());

        if (userRepository.existsByUsername(data.username())) {
            throw new DuplicateUsernameException(data.username());
        }
        if (userRepository.existsByEmail(data.email())) {
            throw new DuplicateEmailException(data.email());
        }

        User user = userMapper.toEntity(data);
        user.setPasswordHash(passwordEncoder.encode(data.password()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
