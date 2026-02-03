package com.essence.essencebackend.music.album.mapper;

import com.essence.essencebackend.music.album.dto.AlbumRequestDTO;
import com.essence.essencebackend.music.album.dto.AlbumResponseDTO;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Album toEntity(AlbumRequestDTO toDto);

    AlbumResponseDTO toDto(Album toEntity);

    List<AlbumResponseSimpleDTO> toListDto(List<Album> toEntity);
}
