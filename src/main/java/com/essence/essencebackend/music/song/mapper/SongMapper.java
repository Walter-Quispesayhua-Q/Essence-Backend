package com.essence.essencebackend.music.song.mapper;

import com.essence.essencebackend.music.song.dto.SongRequestDTO;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SongMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hlsMasterKey", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalPlays", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Song toEntity(SongRequestDTO toDto);

    SongResponseDTO toDto(Song toEntity);
}
