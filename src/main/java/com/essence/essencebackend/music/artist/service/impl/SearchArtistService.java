package com.essence.essencebackend.music.artist.service.impl;

import com.essence.essencebackend.music.artist.model.Artist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchArtistService {

    private final Optional<StreamingService> streamingService;

    public List<Artist> searchArtistsSecondItems(String artists) {

        String[] artistNames = artists.split(" y | & |, ");
    }
}
