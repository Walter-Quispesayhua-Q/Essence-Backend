package com.essence.essencebackend.music.artist.service.impl;

import com.essence.essencebackend.music.artist.mapper.ArtistMapperByInfo;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import com.essence.essencebackend.music.artist.service.ArtistOfSongService;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
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

                Artist artistP = artistMapperByInfo.mapToArtist(artistInfo, artistUrl);
                artistsForSave.add(artistP);
            } catch (Exception e) {
                log.error("Error obteniendo artista: {}", e.getMessage());
                return null;
            }
        }

        List<String> namesSecond = secondaryArtistByNames(artistsNames);

        for (String name : namesSecond) {
            String normalizedArtists = artistMapperByInfo.normalizeForSearch(name);
            Optional<Artist> artist = artistRepository.findByNameNormalized(normalizedArtists);
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
        artistRepository.saveAll(artistsForSave);
        Set<Artist> artists = new LinkedHashSet<>(artistsExist);
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
}
