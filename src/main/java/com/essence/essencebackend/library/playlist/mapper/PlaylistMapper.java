package com.essence.essencebackend.library.playlist.mapper;

import com.essence.essencebackend.library.playlist.dto.PlaylistCreateRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistEditResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistUpdateRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.model.PlaylistType;
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
    @Mapping(target = "totalLikes", ignore = true)
    @Mapping(target = "type", ignore = true)
    Playlist toEntity(PlaylistRequestDTO toDto);

    @Mapping(target = "playlistId", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "playlistSongs", ignore = true)
    @Mapping(target = "playlistLikes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "totalLikes", ignore = true)
    @Mapping(target = "type", ignore = true)
    Playlist toEntity(PlaylistCreateRequestDTO toDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "playlistId", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "playlistSongs", ignore = true)
    @Mapping(target = "playlistLikes", ignore = true)
    @Mapping(target = "totalLikes", ignore = true)
    @Mapping(target = "type", ignore = true)
    Playlist toUpdateEntity(PlaylistRequestDTO toUpdate, @MappingTarget Playlist playlist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "playlistId", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "playlistSongs", ignore = true)
    @Mapping(target = "playlistLikes", ignore = true)
    @Mapping(target = "totalLikes", ignore = true)
    @Mapping(target = "type", ignore = true)
    Playlist toUpdateEntity(PlaylistUpdateRequestDTO toUpdate, @MappingTarget Playlist playlist);

    @Mapping(source = "playlistId", target = "id")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToLocalDate")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToLocalDate")
    @Mapping(target = "totalLikes", expression = "java(mapTotalLikes(toEntity))")
    @Mapping(target = "totalSongs", expression = "java(mapTotalSongs(toEntity))")
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(source = "type", target = "type", qualifiedByName = "typeToString")
    PlaylistResponseDTO toDto(Playlist toEntity);

    @Mapping(source = "playlistId", target = "id")
    @Mapping(target = "totalLikes", expression = "java(mapTotalLikes(toEntity))")
    @Mapping(source = "type", target = "type", qualifiedByName = "typeToString")
    PlaylistSimpleResponseDTO toDtoSimple(Playlist toEntity);

    @Mapping(source = "playlistId", target = "id")
    PlaylistEditResponseDTO toEditDto(Playlist toEntity);


    @Named("instantToLocalDate")
    default LocalDate instantToLocalDate(Instant date) {
        if (date == null) return null;
        return LocalDate.ofInstant(date, ZoneId.systemDefault());
    }

    // Sin @Named, usa expression = "java(...)"
    default Long mapTotalLikes(Playlist playlist) {
        if (playlist == null || !playlist.getIsPublic()) {
            return null;
        }
        return playlist.getTotalLikes();
    }
    default Integer mapTotalSongs(Playlist playlist) {
        if (playlist == null || playlist.getPlaylistSongs() == null) {
            return 0;
        }
        return playlist.getPlaylistSongs().size();
    }

    @Named("typeToString")
    default String typeToString(PlaylistType type) {
        return type != null ? type.name() : "NORMAL";
    }
}
