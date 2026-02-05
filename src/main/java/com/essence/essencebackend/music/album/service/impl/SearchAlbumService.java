package com.essence.essencebackend.music.album.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchAlbumService {

    private final Optional<StreamingService> streamingService;

    private PlaylistInfoItem searchAlbumItem(String songName, String artistName) {
        String query = songName + " " + artistName;

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
                    if (albumItem.getUploaderName().equalsIgnoreCase(artistName)) {
                        return albumItem;
                    }
                }
            }
            if (!albumSearch.getRelatedItems().isEmpty()) {
                return (PlaylistInfoItem) albumSearch.getRelatedItems().get(0);
            }
        } catch (Exception e) {
            log.error("Error buscando álbum: {}", e.getMessage());
        }
        return null;
    }

    public String getAlbumUrl(String songName, String artistName) {
        PlaylistInfoItem item = searchAlbumItem(songName, artistName);
        return item != null ? item.getUrl() : null;
    }

    public PlaylistInfo getAlbumInfoByUrl(String albumUrl) {
        try {
            return PlaylistInfo.getInfo(streamingService.get(), albumUrl);
        } catch (Exception e) {
            log.error("Error obteniendo álbum por URL: {}", e.getMessage());
            return null;
        }
    }

}