package com.essence.essencebackend.music.artist.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchArtistService {

    private final Optional<StreamingService> streamingService;

    public  ChannelInfoItem searchArtistsItems(String artistName) {
        log.info("Buscando artista por nombre: {}", artistName);

        try {
            SearchInfo search = SearchInfo.getInfo(
                    streamingService.get(),
                    streamingService.get().getSearchQHFactory().fromQuery(
                            artistName,
                            List.of("music_artists"),
                            ""
                    )
            );
            if (!search.getRelatedItems().isEmpty()) {
                return (ChannelInfoItem) search.getRelatedItems().get(0);
            }
        } catch (Exception e) {
            log.error("Error buscando artista: {}", e.getMessage());
        }

        return null;
    }



}
