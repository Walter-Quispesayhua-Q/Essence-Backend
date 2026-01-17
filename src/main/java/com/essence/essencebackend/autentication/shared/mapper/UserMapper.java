package com.essence.essencebackend.autentication.shared.mapper;

import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.autentication.register.dto.RegisterRequestDTO;
import com.essence.essencebackend.autentication.register.dto.RegisterResponseDTO;
import com.essence.essencebackend.autentication.shared.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //== register
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(RegisterRequestDTO toDTO);

    RegisterResponseDTO toDto(User toEntity);
    //==

    //== login
    LoginResponseDTO toLoginDTO(User toEntity);


}
