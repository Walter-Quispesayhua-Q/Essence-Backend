package com.essence.essencebackend.library.playlist.mapper;

import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;
import com.essence.essencebackend.library.playlist.model.Playlist;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {

    @Mapping(target = "playlistId", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "playlistSongs", ignore = true)
    @Mapping(target = "playlistLikes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Playlist toEntity(PlaylistRequestDTO toDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Playlist toUpdateEntity(PlaylistRequestDTO toUpdate, @MappingTarget Playlist playlist);

    @Mapping(source = "playlistId", target = "id")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToLocalDate")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToLocalDate")
    @Mapping(source = "totalLikes", target = "totalLikes", qualifiedByName = "mapTotalLikes")
    PlaylistResponseDTO toDto(Playlist toEntity);


    @Mapping(source = "totalLikes", target = "totalLikes", qualifiedByName = "mapTotalLikes")
    PlaylistSimpleResponseDTO toDtoSimple(Playlist toEntity);


    @Named("instantToLocalDate")
    default LocalDate instantToLocalDate(Instant date) {
        return LocalDate.ofInstant(date, ZoneId.systemDefault());
    }

    @Named("mapTotalLikes")
    default Long mapTotalLikesIfPublic(Playlist playlist) {
        if (!playlist.getIsPublic()) {
            return null;
        }
        return playlist.getTotalLikes();
    }
}
