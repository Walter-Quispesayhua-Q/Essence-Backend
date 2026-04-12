package com.essence.essencebackend.music.artist.service.impl;


import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.library.like.repository.ArtistLikeRepository;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.mapper.AlbumMapperByInfo;
import com.essence.essencebackend.music.artist.dto.ArtistsResponseDTO;
import com.essence.essencebackend.music.artist.mapper.ArtistMapperByInfo;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import com.essence.essencebackend.music.artist.service.ArtistService;
import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.service.UrlBuilder;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.mapper.SongMapperByInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabs;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final UrlBuilder urlBuilder;
    private final Optional<StreamingService> streamingService;
    private final SongMapperByInfo songMapperByInfo;
    private final AlbumMapperByInfo albumMapperByInfo;
    private final ArtistMapperByInfo artistMapperByInfo;
    private final ArtistLikeRepository artistLikeRepository;
    private final UrlExtractor urlExtractor;
    private final ExecutorService executor;

    public ArtistServiceImpl(
            ArtistRepository artistRepository,
            UrlBuilder urlBuilder,
            Optional<StreamingService> streamingService,
            SongMapperByInfo songMapperByInfo,
            AlbumMapperByInfo albumMapperByInfo,
            ArtistMapperByInfo artistMapperByInfo,
            ArtistLikeRepository artistLikeRepository,
            UrlExtractor urlExtractor,
            @Qualifier("smallExecutor") ExecutorService executor
    ) {
        this.artistRepository = artistRepository;
        this.urlBuilder = urlBuilder;
        this.streamingService = streamingService;
        this.songMapperByInfo = songMapperByInfo;
        this.albumMapperByInfo = albumMapperByInfo;
        this.artistMapperByInfo = artistMapperByInfo;
        this.artistLikeRepository = artistLikeRepository;
        this.urlExtractor = urlExtractor;
        this.executor = executor;
    }

    @Override
    public ArtistsResponseDTO getArtistDetail(String username, String artistUrlOrId) {
        log.info("Obteniendo artista por el usuario: {}", username);

        String artistUrl = urlBuilder.resolveUrl(artistUrlOrId, ContentType.ARTIST);
        String artistUrlId = urlExtractor.extractId(artistUrl, ContentType.ARTIST);

        Artist artist = artistRepository.findByArtistUrl(artistUrlId)
                .orElseGet(() -> createArtistFromChannel(artistUrl));

        String artistName = artist.getNameArtist();

        List<ListLinkHandler> tabs = extractChannelTabs(artistUrl);

        CompletableFuture<List<InfoItem>> songsFuture = CompletableFuture.supplyAsync(
                        () -> getSongsForArtistDetail(tabs, artistName), executor)
                .exceptionally(ex -> {
                    log.warn("No se pudo obtener songs: {}", ex.getMessage());
                    return List.of();
                });

        CompletableFuture<List<InfoItem>> albumsFuture = CompletableFuture.supplyAsync(
                        () -> getAlbumsForArtistDetail(tabs, artistName), executor)
                .exceptionally(ex -> {
                    log.warn("No se pudo obtener albums: {}", ex.getMessage());
                    return List.of();
                });

        List<SongResponseSimpleDTO> songs = songsFuture.join().stream()
                .filter(StreamInfoItem.class::isInstance)
                .map(StreamInfoItem.class::cast)
                .map(songMapperByInfo::mapFromItem)
                .limit(15)
                .toList();

        List<AlbumResponseSimpleDTO> albums = albumsFuture.join().stream()
                .filter(PlaylistInfoItem.class::isInstance)
                .map(PlaylistInfoItem.class::cast)
                .map(albumMapperByInfo::mapFromItem)
                .toList();

        boolean isLiked = artistLikeRepository.existsByArtistIdAndUsername(
                artist.getId(), username
        );
        return new ArtistsResponseDTO(
                artist.getId(),
                artist.getNameArtist(),
                artist.getDescription(),
                artist.getImageKey(),
                artist.getArtistUrl(),
                artist.getCountry(),
                albums,
                songs,
                isLiked
        );
    }

    private Artist createArtistFromChannel(String artistUrl) {
        String artistUrlId = urlExtractor.extractId(artistUrl, ContentType.ARTIST);
        try {
            ChannelInfo artistInfo = ChannelInfo.getInfo(streamingService.get(), artistUrl);
            return artistRepository.save(artistMapperByInfo.mapToArtist(artistInfo, artistUrl));
        } catch (DataIntegrityViolationException e) {
            log.info("Artista ya existe, re-fetching: {}", artistUrlId);
            return artistRepository.findByArtistUrl(artistUrlId)
                    .orElseThrow(ExtractionServiceUnavailableException::new);
        } catch (Exception e) {
            log.error("Error al crear artista {}: {}", artistUrl, e.getMessage(), e);
            throw new ExtractionServiceUnavailableException();
        }
    }

    private List<ListLinkHandler> extractChannelTabs(String artistUrl) {
        try {
            ChannelExtractor channelExtractor = streamingService.get()
                    .getChannelExtractor(artistUrl);
            channelExtractor.fetchPage();
            return channelExtractor.getTabs();
        } catch (Exception e) {
            log.warn("Error extrayendo tabs del canal {}: {}", artistUrl, e.getMessage());
            return List.of();
        }
    }

    private List<InfoItem> getSongsForArtistDetail(List<ListLinkHandler> tabs, String artistName) {
        List<InfoItem> items = getTabItems(tabs, ChannelTabs.TRACKS);
        if (!items.isEmpty()) return items;

        items = getTabItems(tabs, ChannelTabs.VIDEOS);
        if (!items.isEmpty()) return items;

        log.info("Usando search fallback para canciones de: {}", artistName);
        return searchFallback(artistName, "music_songs");
    }

    private List<InfoItem> getAlbumsForArtistDetail(List<ListLinkHandler> tabs, String artistName) {

        List<InfoItem> items = getTabItems(tabs, ChannelTabs.ALBUMS);
        if (!items.isEmpty()) return items;

        items = getTabItems(tabs, ChannelTabs.PLAYLISTS);
        if (!items.isEmpty()) return items;

        log.info("Usando search fallback para álbumes de: {}", artistName);
        return searchFallback(artistName, "music_albums");
    }

    private List<InfoItem> searchFallback(String artistName, String filter) {
        try {
            SearchExtractor extractor = streamingService.get()
                    .getSearchExtractor(artistName, List.of(filter), "");
            extractor.fetchPage();
            return extractor.getInitialPage().getItems();
        } catch (Exception e) {
            log.warn("Search fallback falló para {}: {}", artistName, e.getMessage());
            return List.of();
        }
    }

    private List<InfoItem> getTabItems(List<ListLinkHandler> tabs, String tabName) {
        try {
            Optional<ListLinkHandler> tab = tabs.stream()
                    .filter(t -> t.getContentFilters().contains(tabName))
                    .findFirst();

            if (tab.isEmpty()) {
                return List.of();
            }

            ChannelTabExtractor tabExtractor = streamingService.get()
                    .getChannelTabExtractor(tab.get());
            tabExtractor.fetchPage();

            return tabExtractor.getInitialPage().getItems();
        } catch (Exception e) {
            log.warn("Error obteniendo tab {}: {}", tabName, e.getMessage());
            return List.of();
        }
    }
}