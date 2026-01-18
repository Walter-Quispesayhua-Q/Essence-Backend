package com.essence.essencebackend.music.artist.mapper;

import com.essence.essencebackend.music.artist.dto.ArtistRequestDTO;
import com.essence.essencebackend.music.artist.dto.ArtistsResponseDTO;
import com.essence.essencebackend.music.artist.model.Artist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Artist toEntity(ArtistRequestDTO toDto);

    ArtistsResponseDTO toDto(Artist toEntity);
}
