package com.essence.essencebackend.music.song.mapper;

import com.essence.essencebackend.music.song.dto.SongRequestDTO;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SongMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hlsMasterKey", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalPlays", ignore = true)
    @Mapping(target = "song_type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Song toEntity(SongRequestDTO toDto);

    SongResponseDTO toDto(Song toEntity);

    List<SongResponseSimpleDTO> toListDto(List<Song> toListEntity);
}
