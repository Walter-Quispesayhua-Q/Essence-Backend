package com.essence.essencebackend.music.song.mapper;

import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SongMapper {

    // para historial
    @Mapping(source = "id", target = "id")
    @Mapping(source = "totalStreams", target = "totalPlays")
    @Mapping(target = "artistName", expression = "java(getPrimaryArtistName(song))")
    @Mapping(target = "albumName", expression = "java(getAlbumName(song))")
    SongResponseSimpleDTO toSimpleDto(Song song);

    @Mapping(source = "song.totalStreams", target = "totalPlays")
    @Mapping(target = "artists", expression = "java(mapArtists(song))")
    @Mapping(source = "song.streamingUrl", target = "streamingUrl")
    @Mapping(source = "isLiked", target = "isLiked")
    @Mapping(target = "streamingUrlExpiresAt", expression = "java(calculateUrlExpiration(song))")
    SongResponseDTO toFullDto(Song song, boolean isLiked);

    List<SongResponseSimpleDTO> toListDto(List<Song> toListEntity);

    default Instant calculateUrlExpiration(Song song) {
        if (song == null || song.getLastSyncedAt() == null) return null;
        return song.getLastSyncedAt().plus(Duration.ofMinutes(300));
    }

    @Named("instantToLocalDate")
    default LocalDate instantToLocalDate(Instant date) {
        if (date == null) return null;
        return LocalDate.ofInstant(date, ZoneId.systemDefault());
    }

    default String getPrimaryArtistName(Song song) {
        if (song == null || song.getSongArtists() == null || song.getSongArtists().isEmpty()) {
            return null;
        }
        return song.getSongArtists().stream()
                .filter(sa -> sa.getIsPrimary() != null && sa.getIsPrimary())
                .findFirst()
                .map(sa -> sa.getArtist().getNameArtist())
                .orElse(song.getSongArtists().get(0).getArtist().getNameArtist());
    }

    default String getAlbumName(Song song) {
        if (song == null || song.getAlbum() == null) {
            return null;
        }
        return song.getAlbum().getTitle();
    }

    default List<ArtistResponseSimpleDTO> mapArtists(Song song) {
        if (song == null || song.getSongArtists() == null) {
            return null;
        }
        return song.getSongArtists().stream()
                .map(sa -> new ArtistResponseSimpleDTO(
                        sa.getArtist().getId(),
                        sa.getArtist().getNameArtist(),
                        sa.getArtist().getImageKey(),
                        sa.getArtist().getArtistUrl()
                ))
                .toList();
    }
}