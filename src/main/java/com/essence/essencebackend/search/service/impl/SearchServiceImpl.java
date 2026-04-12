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
import org.schabi.newpipe.extractor.ListExtractor;
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

    private static final int SEARCH_ALL_LIMIT = 15;
    private static final int MAX_FILTER_PAGE = 2;

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
    public SearchResponseDTO search(String query, String type, int page) {
        int safePage = Math.max(0, Math.min(page, MAX_FILTER_PAGE));
        log.info("Buscando: {} tipo: {} página: {}", query, type, safePage);

        String typeSearch = resolveType(query, type);
        String queryClean = SearchType.cleanQuery(query);

        if (queryClean.isBlank()) {
            throw new IllegalArgumentException("La busqueda no puede estar vacia o contener solo palabras reservadas.");
        }

        try {
            if (typeSearch != null) {
                return searchWithFilter(queryClean, typeSearch, safePage);
            }
            return searchAll(queryClean);
        } catch (ExtractionServiceUnavailableException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error en búsqueda: {}", e.getMessage(), e);
            throw new ExtractionServiceUnavailableException();
        }
    }

//  metodo para reutilizar en otros modulos
    @Override
    public List<InfoItem> searchByFilter(String query, String filter, int limit) {
        try {
            List<InfoItem> items = searchByType(query, filter);
            return items.stream().limit(limit).toList();
        } catch (Exception e) {
            log.warn("searchByFilter falló para {}: {}", filter, e.getMessage(), e);
            return List.of();
        }
    }

    private SearchResponseDTO searchWithFilter(String query, String filter, int page) throws Exception {
        SearchExtractor extractor = getService()
                .getSearchExtractor(query, List.of(filter), "");
        extractor.fetchPage();

        // Navegar a la página solicitada
        ListExtractor.InfoItemsPage<?> currentPage = extractor.getInitialPage();
        for (int i = 0; i < page && currentPage.hasNextPage(); i++) {
            currentPage = extractor.getPage(currentPage.getNextPage());
        }

        List<InfoItem> items = (List<InfoItem>) currentPage.getItems();
        boolean hasNext = currentPage.hasNextPage();

        return new SearchResponseDTO(
                extractSongs(items),
                extractAlbums(items),
                extractArtists(items),
                hasNext
        );
    }

    private SearchResponseDTO searchAll(String query) throws Exception {
        List<SongResponseSimpleDTO> songs = extractSongs(searchByType(query, "music_songs"))
                .stream().limit(SEARCH_ALL_LIMIT).toList();

        List<AlbumResponseSimpleDTO> albums = extractAlbums(searchByType(query, "music_albums"))
                .stream().limit(SEARCH_ALL_LIMIT).toList();

        List<ArtistResponseSimpleDTO> artists = extractArtists(searchByType(query, "music_artists"))
                .stream().limit(SEARCH_ALL_LIMIT).toList();

        return new SearchResponseDTO(songs, albums, artists, false);
    }

    private List<InfoItem> searchByType(String query, String filter) throws Exception {
        SearchExtractor extractor = getService()
                .getSearchExtractor(query, List.of(filter), "");
        extractor.fetchPage();
        return extractor.getInitialPage().getItems();
    }

    private StreamingService getService() {
        return streamingService.orElseThrow(ExtractionServiceUnavailableException::new);
    }

    private List<SongResponseSimpleDTO> extractSongs(List<InfoItem> items) {
        return items.stream()
                .filter(StreamInfoItem.class::isInstance)
                .map(StreamInfoItem.class::cast)
                .map(songMapperByInfo::mapFromItem)
                .toList();
    }

    private List<AlbumResponseSimpleDTO> extractAlbums(List<InfoItem> items) {
        return items.stream()
                .filter(PlaylistInfoItem.class::isInstance)
                .map(PlaylistInfoItem.class::cast)
                .map(albumMapperByInfo::mapFromItem)
                .toList();
    }

    private List<ArtistResponseSimpleDTO> extractArtists(List<InfoItem> items) {
        return items.stream()
                .filter(ChannelInfoItem.class::isInstance)
                .map(ChannelInfoItem.class::cast)
                .map(artistMapperByInfo::mapFromItem)
                .toList();
    }

    private String resolveType(String query, String type) {
        String detected = SearchType.detectFromQuery(query);
        if (detected != null) {
            return detected;
        }
        return SearchType.fromValue(type);
    }
}