package com.essence.essencebackend.music.artist.service.impl;

import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import com.essence.essencebackend.music.artist.service.ArtistService;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final Optional<StreamingService> streamingService;
    private final UrlExtractor urlExtractor;

    @Override
    public List<Artist> getOrCreateArtistBySong(String artistUrl) {
        log.info("Obteniendo artista por su url: {}", artistUrl);

        String artistUrlId = urlExtractor.extractId(artistUrl, UrlExtractor.ContentType.ARTIST);
        Optional<List<Artist>> artistExits = artistRepository.findByArtistUrl(artistUrlId);

        if (artistExits.isPresent()) {
            return artistExits.get();
        }

        try {
            ChannelInfo artistInfo = ChannelInfo.getInfo(
                    streamingService.get(),
                    artistUrl
            );
            return artistRepository.saveAll(mapToArtist(artistInfo, artistUrlId));
        }
        catch (Exception e) {
            log.error("Error obteniendo artista: {}", e.getMessage());
            return null;
        }
    }

    public Artist searchArtistByName(String artistName) {
        SearchInfo search = SearchInfo.getInfo(
                streamingService.get(),
                streamingService.get().getSearchQHFactory().fromQuery(
                        artistName,
                        List.of("music_artists"),
                        ""
                )
        );

        ChannelInfoItem item = (ChannelInfoItem) search.getRelatedItems().get(0);
        return mapToArtist(item, item.getUrl());
    }

    private Artist mapToArtistFromItem(ChannelInfoItem item) {
        Artist artist = new Artist();
        artist.setNameArtist(item.getName());
        artist.setImageKey(item.getThumbnails().isEmpty() ? null
                : item.getThumbnails().get(0).getUrl());
        artist.setArtistUrl(urlExtractor.extractId(item.getUrl(), ContentType.ARTIST));
        // description NO disponible en ChannelInfoItem
        return artist;
    }

    private Artist mapToArtist(ChannelInfo info, String artistUrl) {
        Artist artist = new Artist();
        artist.setNameArtist(info.getName());
        artist.setDescription(info.getDescription());
        artist.setImageKey(info.getAvatars().isEmpty() ? null
                : info.getAvatars().get(0).getUrl());
        artist.setArtistUrl(urlExtractor.extractId(artistUrl, ContentType.ARTIST));
        return artist;
    }
}
