package com.essence.essencebackend.music.artist.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.mapper.AlbumMapperByInfo;
import com.essence.essencebackend.music.artist.dto.ArtistsResponseDTO;
import com.essence.essencebackend.music.artist.mapper.ArtistMapperByInfo;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import com.essence.essencebackend.music.artist.service.ArtistService;
import com.essence.essencebackend.music.shared.service.UrlBuilder;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.mapper.SongMapperByInfo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabs;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Slf4j
@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final UrlBuilder urlBuilder;
    private final Optional<StreamingService> streamingService;
    private final SongMapperByInfo songMapperByInfo;
    private final AlbumMapperByInfo albumMapperByInfo;
    private final ArtistMapperByInfo artistMapperByInfo;

    @Resource(name = "smallExecutor")
    private ExecutorService executor;

    @Override
    public ArtistsResponseDTO getArtistDetail(String username, String artistUrlOrId) {
        log.info("Obteniendo artista por el usuario: {}", username);

        String artistUrl = urlBuilder.resolveUrl(artistUrlOrId, UrlBuilder.ContentType.ARTIST);

        Artist artist = artistRepository.findByArtistUrl(artistUrl)
                .orElseGet(() -> createArtistFromChannel(artistUrl));

        String youtubeUrl = toYoutubeUrl(artistUrl);

        CompletableFuture<List<InfoItem>> songsFuture = CompletableFuture.supplyAsync(
                        () -> getSongsForArtistDetail(youtubeUrl), executor)
                .exceptionally(ex -> {
                    log.warn("No se pudo obtener songs: {}", ex.getMessage());
                    return List.of();
                });

        CompletableFuture<List<InfoItem>> albumsFuture = CompletableFuture.supplyAsync(
                        () -> getAlbumsForArtistDetail(youtubeUrl), executor)
                .exceptionally(ex -> {
                    log.warn("No se pudo obtener albums: {}", ex.getMessage());
                    return List.of();
                });

        List<SongResponseSimpleDTO> songs = songsFuture.join().stream()
                .filter(StreamInfoItem.class::isInstance)
                .map(StreamInfoItem.class::cast)
                .map(songMapperByInfo::mapFromItem)
                .toList();

        List<AlbumResponseSimpleDTO> albums = albumsFuture.join().stream()
                .filter(PlaylistInfoItem.class::isInstance)
                .map(PlaylistInfoItem.class::cast)
                .map(albumMapperByInfo::mapFromItem)
                .toList();

        return new ArtistsResponseDTO(
                artist.getId(),
                artist.getNameArtist(),
                artist.getDescription(),
                artist.getImageKey(),
                artist.getArtistUrl(),
                artist.getCountry(),
                albums,
                songs
        );
    }

    private String toYoutubeUrl(String artistUrl) {
        return artistUrl.replace("music.youtube.com", "www.youtube.com");
    }

    private Artist createArtistFromChannel(String artistUrl) {
        try {
            ChannelInfo artistInfo = ChannelInfo.getInfo(streamingService.get(), artistUrl);
            return artistRepository.save(artistMapperByInfo.mapToArtist(artistInfo, artistUrl));
        } catch (Exception e) {
            log.error("Error al crear artista {}: {}", artistUrl, e.getMessage());
            throw new ExtractionServiceUnavailableException();
        }
    }

    private List<InfoItem> getSongsForArtistDetail(String youtubeUrl) {
        return getTabItems(youtubeUrl, ChannelTabs.VIDEOS);
    }

    private List<InfoItem> getAlbumsForArtistDetail(String youtubeUrl) {
        return getTabItems(youtubeUrl, ChannelTabs.ALBUMS);
    }

    private List<InfoItem> getTabItems(String youtubeUrl, String tabName) {
        try {
            ChannelExtractor channelExtractor = streamingService.get()
                    .getChannelExtractor(youtubeUrl);
            channelExtractor.fetchPage();

            List<ListLinkHandler> tabs = channelExtractor.getTabs();

            Optional<ListLinkHandler> tab = tabs.stream()
                    .filter(t -> t.getContentFilters().contains(tabName))
                    .findFirst();

            if (tab.isEmpty()) {
                log.info("Tab {} no disponible para {}", tabName, youtubeUrl);
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