package com.essence.essencebackend.autentication.register.mapper;

import com.essence.essencebackend.autentication.register.dto.UserRequestDTO;
import com.essence.essencebackend.autentication.register.dto.UserResponseDTO;
import com.essence.essencebackend.autentication.register.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserRequestDTO toDTO);

    UserResponseDTO toDto(User toEntity);
}
