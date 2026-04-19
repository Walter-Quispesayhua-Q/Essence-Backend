package com.essence.essencebackend.music.album.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchAlbumService {

    private final Optional<StreamingService> streamingService;

    public List<String> getAlbumCandidateUrls(String songName, String artistName) {
        String cleanTitle = cleanSongTitle(songName);
        String query = cleanTitle + " " + artistName;
        List<String> candidates = new ArrayList<>();

        log.info("Buscando albums candidatos: query='{}'", query);

        try {
            SearchInfo albumSearch = SearchInfo.getInfo(
                    streamingService.get(),
                    streamingService.get().getSearchQHFactory().fromQuery(
                            query,
                            List.of("music_albums"),
                            ""
                    )
            );

            for (InfoItem item : albumSearch.getRelatedItems()) {
                if (item instanceof PlaylistInfoItem albumItem) {
                    if (isArtistMatch(albumItem.getUploaderName(), artistName)) {
                        candidates.add(albumItem.getUrl());
                        log.info("Candidato: '{}' by '{}'", albumItem.getName(), albumItem.getUploaderName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error buscando álbum: {}", e.getMessage(), e);
        }

        return candidates;
    }

    public PlaylistInfo getAlbumInfoByUrl(String albumUrl) {
        try {
            return PlaylistInfo.getInfo(streamingService.get(), albumUrl);
        } catch (Exception e) {
            log.error("Error obteniendo álbum por URL: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean albumContainsSong(PlaylistInfo albumInfo, String songName) {
        if (albumInfo == null) return false;
        String normalizedSong = normalize(cleanSongTitle(songName));
        return albumInfo.getRelatedItems().stream()
                .anyMatch(track -> {
                    String normalizedTrack = normalize(track.getName());
                    return normalizedTrack.contains(normalizedSong)
                            || normalizedSong.contains(normalizedTrack);
                });
    }

    private boolean isArtistMatch(String uploaderName, String artistName) {
        if (uploaderName == null || artistName == null) return false;
        String normalizedUploader = normalize(uploaderName);
        String normalizedArtist = normalize(artistName);
        return normalizedUploader.contains(normalizedArtist)
                || normalizedArtist.contains(normalizedUploader);
    }

    private String cleanSongTitle(String title) {
        return title
                .replaceAll("\\(.*?\\)", "")
                .replaceAll("\\[.*?\\]", "")
                .replaceAll("(?i)official.*", "")
                .replaceAll("(?i)music video.*", "")
                .replaceAll("(?i)lyric.*", "")
                .replaceAll("(?i)audio.*", "")
                .replaceAll("(?i)ft\\.?\\s.*", "")
                .replaceAll("(?i)feat\\.?\\s.*", "")
                .trim();
    }

    private String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[\\p{M}]", "");
        return normalized.toLowerCase().trim();
    }
}