package com.essence.essencebackend.library.history.mapper;

import com.essence.essencebackend.library.history.dto.PlayHistoryRequestDTO;
import com.essence.essencebackend.library.history.model.PlayHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayHistoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "song", ignore = true)
    @Mapping(target = "playedAt", ignore = true)
    PlayHistory toEntity(PlayHistoryRequestDTO toDto);
}
