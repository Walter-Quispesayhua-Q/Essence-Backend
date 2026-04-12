package com.essence.essencebackend.music.album.mapper;


import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.model.AlbumArtist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    @Mapping(target = "artists", source = "albumArtists", qualifiedByName = "mapArtistNames")
    AlbumResponseSimpleDTO toDto(Album album);

    List<AlbumResponseSimpleDTO> toListDto(List<Album> albums);

    @Named("mapArtistNames")
    default List<String> mapArtistNames(List<AlbumArtist> albumArtists) {
        if (albumArtists == null || albumArtists.isEmpty()) {
            return Collections.emptyList();
        }
        return albumArtists.stream()
                .map(aa -> aa.getArtist().getNameArtist())
                .toList();
    }
}
