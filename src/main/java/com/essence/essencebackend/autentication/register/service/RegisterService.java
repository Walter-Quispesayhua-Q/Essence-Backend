package com.essence.essencebackend.autentication.register.service;

import com.essence.essencebackend.autentication.register.dto.RegisterRequestDTO;
import com.essence.essencebackend.autentication.register.dto.RegisterResponseDTO;

public interface RegisterService {
    boolean getAvailableUsername(String username);
    RegisterResponseDTO createUser(RegisterRequestDTO data);
}
