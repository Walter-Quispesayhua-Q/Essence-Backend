package com.essence.essencebackend.music.album.mapper;


import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.model.Album;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    List<AlbumResponseSimpleDTO> toListDto(List<Album> toEntity);
}
