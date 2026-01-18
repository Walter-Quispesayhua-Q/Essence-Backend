package com.essence.essencebackend.library.playlist.mapper;

import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.model.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {

    @Mapping(target = "playlistId", ignore = true)
    @Mapping(target = "coverKey", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "playlistSongs", ignore = true)
    @Mapping(target = "playlistLikes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Playlist toEntity(PlaylistRequestDTO toDto);

    @Mapping(source = "playlistId", target = "id")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToLocalDate")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToLocalDate")
    @Mapping(target = "totalSongs", ignore = true)
    @Mapping(target = "totalLikes", ignore = true)
    PlaylistResponseDTO toDto(Playlist toEntity);

    @Named("instantToLocalDate")
    default LocalDate instantToLocalDate(Instant date) {
        return LocalDate.ofInstant(date, ZoneId.systemDefault());
    }
}
