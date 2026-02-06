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
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final Optional<StreamingService> streamingService;
    private final UrlExtractor urlExtractor;
    private final SearchArtistService searchArtistService;

    @Override
    public Set<Artist> getOrCreateArtistBySong(String artistUrl, String artistsNames) {
        log.info("Obteniendo artista por su url: {}", artistUrl);

        Set<Artist> artistsExist = new HashSet<>();
        Set<Artist> artistsForSave = new HashSet<>();

        String artistaUrlId = urlExtractor.extractId(artistUrl, UrlExtractor.ContentType.ARTIST);

        Optional<Artist> artistExistUrl = artistRepository.findByArtistUrl(artistaUrlId);
        if (artistExistUrl.isPresent()) {
            Artist artistExisting =  artistExistUrl.get();
            if (artistExisting.getDescription() == null) {
               try {
                   ChannelInfo info = ChannelInfo.getInfo(streamingService.get(), artistUrl);
                   artistExisting.setDescription(info.getDescription());
                   artistRepository.save(artistExisting);
               } catch (Exception e) {
                   log.error("Error al completar datos para el artista: {}", e.getMessage());
               }
            }
            artistsExist.add(artistExisting);
        } else {
            try {
                ChannelInfo artistInfo = ChannelInfo.getInfo(
                        streamingService.get(),
                        artistUrl
                );

                Artist artistP = mapToArtist(artistInfo, artistUrl);
                artistsForSave.add(artistP);
            } catch (Exception e) {
                log.error("Error obteniendo artista: {}", e.getMessage());
                return null;
            }
        }

        List<String> namesSecond = secondaryArtistByNames(artistsNames);

        for (String name : namesSecond) {
            String normalizedArtists = normalizeForSearch(name);
            Optional<Artist> artist = artistRepository.findByNameNormalized(normalizedArtists);
            if (artist.isPresent()) {
                artistsExist.add(artist.get());
            } else {
                ChannelInfoItem item = searchArtistService.searchArtistsItems(name);
                if (item == null){
                    log.info("no se pudo obtener los datos de este artista: {} , continuamos con el siguiente", name);
                    continue;
                }
                Artist artistSecond = mapToArtistFromItem(item);
                artistsForSave.add(artistSecond);
            }

        }
        artistRepository.saveAll(artistsForSave);
        Set<Artist> artists = new HashSet<>(artistsExist);
        artists.addAll(artistsForSave);
        return artists;
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

    private String normalizeForSearch(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase().trim();
    }

    private Artist mapToArtist(ChannelInfo info, String artistUrl) {
        Artist artist = new Artist();
        artist.setNameArtist(info.getName());
        artist.setDescription(info.getDescription());
        artist.setImageKey(info.getAvatars().isEmpty() ? null
                : info.getAvatars().get(0).getUrl());
        artist.setArtistUrl(urlExtractor.extractId(artistUrl, UrlExtractor.ContentType.ARTIST));
        artist.setNameNormalized(normalizeForSearch(info.getName()));
        return artist;
    }

    private Artist mapToArtistFromItem(ChannelInfoItem item) {
        Artist artist = new Artist();
        artist.setNameArtist(item.getName());
        artist.setImageKey(item.getThumbnails().isEmpty() ? null
                : item.getThumbnails().get(0).getUrl());
        artist.setArtistUrl(urlExtractor.extractId(item.getUrl(), UrlExtractor.ContentType.ARTIST));
        artist.setNameNormalized(normalizeForSearch(item.getName()));
        return artist;
    }
}
