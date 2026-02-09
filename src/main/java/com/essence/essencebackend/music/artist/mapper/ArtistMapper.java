package com.essence.essencebackend.music.artist.mapper;

import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.artist.model.Artist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    List<ArtistResponseSimpleDTO> toListDto(List<Artist> toEntity);
}
