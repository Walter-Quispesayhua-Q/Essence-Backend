package com.essence.essencebackend.music.artist.service.impl;

import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.music.artist.mapper.ArtistMapperByInfo;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import com.essence.essencebackend.music.artist.service.ArtistOfSongService;
import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ArtistOfSongServiceImpl implements ArtistOfSongService {

    private final ArtistRepository artistRepository;
    private final Optional<StreamingService> streamingService;
    private final UrlExtractor urlExtractor;
    private final SearchArtistService searchArtistService;
    private final ArtistMapperByInfo artistMapperByInfo;

    @Override
    public Set<Artist> getOrCreateArtistBySong(String artistUrl, String artistsNames) {
        log.info("Obteniendo artista por su url: {}", artistUrl);

        Set<Artist> artistsExist = new LinkedHashSet<>();
        Set<Artist> artistsForSave = new LinkedHashSet<>();

        String artistaUrlId = urlExtractor.extractId(artistUrl, ContentType.ARTIST);

        Optional<Artist> artistExistUrl = artistRepository.findByArtistUrl(artistaUrlId);
        if (artistExistUrl.isPresent()) {
            Artist artistExisting = artistExistUrl.get();
            if (artistExisting.getDescription() == null) {
                try {
                    ChannelInfo info = ChannelInfo.getInfo(streamingService.get(), artistUrl);
                    artistExisting.setDescription(info.getDescription());
                    artistRepository.save(artistExisting);
                } catch (Exception e) {
                    log.error("Error al completar datos para el artista: {}", e.getMessage(), e);
                }
            }
            artistsExist.add(artistExisting);
        } else {
            try {
                ChannelInfo artistInfo = ChannelInfo.getInfo(streamingService.get(), artistUrl);
                String cleanName = artistMapperByInfo.cleanArtistName(artistInfo.getName());
                String normalized = artistMapperByInfo.normalizeForSearch(cleanName);
                boolean isTopic = artistMapperByInfo.isTopic(artistInfo.getName());

                Optional<Artist> existByName = artistRepository.findFirstByNameNormalized(normalized);

                if (existByName.isPresent()) {
                    Artist existing = existByName.get();
                    if (!isTopic) {
                        log.info("Actualizando artista {} de Topic a Oficial", cleanName);
                        existing.setArtistUrl(artistaUrlId);
                        existing.setImageKey(artistInfo.getAvatars().stream()
                                .max(Comparator.comparing(org.schabi.newpipe.extractor.Image::getHeight))
                                .map(org.schabi.newpipe.extractor.Image::getUrl)
                                .orElse(existing.getImageKey()));
                        existing.setDescription(artistInfo.getDescription());
                        artistRepository.save(existing);
                    }
                    artistsExist.add(existing);
                } else {
                    Artist artistP = artistMapperByInfo.mapToArtist(artistInfo, artistUrl);
                    artistsForSave.add(artistP);
                }
            } catch (Exception e) {
                log.error("Error obteniendo artista: {}", e.getMessage(), e);
                throw new ExtractionServiceUnavailableException();
            }
        }

        List<String> namesSecond = secondaryArtistByNames(artistsNames);

        for (String name : namesSecond) {
            String normalizedArtists = artistMapperByInfo.normalizeForSearch(name);
            Optional<Artist> artist = artistRepository.findFirstByNameNormalized(normalizedArtists);
            if (artist.isPresent()) {
                artistsExist.add(artist.get());
            } else {
                ChannelInfoItem item = searchArtistService.searchArtistsItems(name);
                if (item == null){
                    log.info("no se pudo obtener los datos de este artista: {} , continuamos con el siguiente", name);
                    continue;
                }
                Artist artistSecond = artistMapperByInfo.mapToArtistFromItem(item);
                artistsForSave.add(artistSecond);
            }
        }

        saveArtistsResilient(artistsForSave, artistsExist);

        Set<Artist> artists = new LinkedHashSet<>(artistsExist);
        artists.addAll(artistsForSave);
        return artists;
    }

    private void saveArtistsResilient(Set<Artist> artistsForSave, Set<Artist> artistsExist) {
        if (artistsForSave.isEmpty()) return;

        Set<Artist> saved = new LinkedHashSet<>();
        for (Artist artist : artistsForSave) {
            try {
                saved.add(artistRepository.save(artist));
            } catch (DataIntegrityViolationException ex) {
                log.info("Artista ya existe, re-fetching: {}", artist.getArtistUrl());
                artistRepository.findByArtistUrl(artist.getArtistUrl())
                        .ifPresent(artistsExist::add);
            }
        }
        artistsForSave.clear();
        artistsForSave.addAll(saved);
    }

    private List<String> secondaryArtistByNames(String allArtists) {
        if (allArtists == null || allArtists.isBlank()) {
            return List.of();
        }

        String[] names = allArtists.split(" y | & |, ");

        if (names.length <= 1) {
            return List.of();
        }

        return Arrays.stream(names)
                .skip(1)
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .toList();
    }
}