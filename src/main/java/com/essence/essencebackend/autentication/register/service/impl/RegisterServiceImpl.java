package com.essence.essencebackend.autentication.register.service.impl;

import com.essence.essencebackend.autentication.register.dto.RegisterRequestDTO;
import com.essence.essencebackend.autentication.register.dto.RegisterResponseDTO;
import com.essence.essencebackend.autentication.register.exception.DuplicateEmailException;
import com.essence.essencebackend.autentication.register.exception.DuplicateUsernameException;
import com.essence.essencebackend.autentication.shared.mapper.UserMapper;
import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.autentication.register.service.RegisterService;
import jakarta.transaction.Transactional;
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
        log.info("Verificando si usuario esta disponible/libre: {}", username);
        if (userRepository.existsByUsername(username)){
            throw new DuplicateUsernameException(username);
        }
        return true;
    }

    @Override
    @Transactional
    public RegisterResponseDTO createUser(RegisterRequestDTO data) {
        log.info("Iniciando la creación de un usuario: {}", data);
        if (userRepository.existsByEmail(data.email())) {
            throw new DuplicateEmailException(data.email());
        }
        User user = userMapper.toEntity(data);
        user.setPasswordHash(passwordEncoder.encode(data.password()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
