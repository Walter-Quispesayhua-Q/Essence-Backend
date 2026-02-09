package com.essence.essencebackend.user.service;

import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.user.dto.UserDetailDTO;

public interface UserService {

    LoginResponseDTO getCurrentUser(String username);

    UserDetailDTO getUserProfile(String username);
}
