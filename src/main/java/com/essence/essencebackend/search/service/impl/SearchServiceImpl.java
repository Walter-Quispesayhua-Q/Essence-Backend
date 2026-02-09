package com.essence.essencebackend.search.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.mapper.AlbumMapperByInfo;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.artist.mapper.ArtistMapperByInfo;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.mapper.SongMapperByInfo;
import com.essence.essencebackend.search.dto.CategoryDTO;
import com.essence.essencebackend.search.dto.SearchResponseDTO;
import com.essence.essencebackend.search.dto.SearchType;
import com.essence.essencebackend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    private final Optional<StreamingService> streamingService;
    private final SongMapperByInfo songMapperByInfo;
    private final AlbumMapperByInfo albumMapperByInfo;
    private final ArtistMapperByInfo artistMapperByInfo;

    @Override
    public List<CategoryDTO> getCategories() {
        log.info("Obteniendo categorías disponibles para la búsqueda");
        return Arrays.stream(SearchType.values())
                .map(type -> new CategoryDTO(type.name(), type.getLabel()))
                .toList();
    }

    @Override
    public SearchResponseDTO search(String query, String type) {
        log.info("Obteniendo resultados para la petición: {} y según su tipo: {}" , query, type);

        String typeSearch = resolveType(query, type);
        String queryClean = SearchType.cleanQuery(query);

        try {
            List<String> filters = (typeSearch != null)
                    ? List.of(typeSearch)
                    : List.of();

            SearchExtractor extractor = streamingService.get()
                    .getSearchExtractor(queryClean, filters, "");

            extractor.fetchPage();
            List<InfoItem> items = extractor.getInitialPage().getItems();

            List<SongResponseSimpleDTO> songs = items.stream()
                    .filter(StreamInfoItem.class::isInstance)
                    .map(StreamInfoItem.class::cast)
                    .map(songMapperByInfo::mapFromItem)
                    .toList();
            List<AlbumResponseSimpleDTO> albums = items.stream()
                    .filter(PlaylistInfoItem.class::isInstance)
                    .map(PlaylistInfoItem.class::cast)
                    .map(albumMapperByInfo::mapFromItem)
                    .toList();
            List<ArtistResponseSimpleDTO> artists = items.stream()
                    .filter(ChannelInfoItem.class::isInstance)
                    .map(ChannelInfoItem.class::cast)
                    .map(artistMapperByInfo::mapFromItem)
                    .toList();

            return new SearchResponseDTO(songs, albums, artists);
        }
        catch (Exception e) {
            log.error("Error en búsqueda: {}", e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }

    }

    private String resolveType(String query, String type) {
        String detected = SearchType.detectFromQuery(query);
        if (detected != null) {
            return detected;
        }
        return SearchType.fromValue(type);
    }
}
