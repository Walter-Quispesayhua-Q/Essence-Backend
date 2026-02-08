package com.essence.essencebackend.music.shared.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UrlBuilder {

    public enum ContentType { SONG, ALBUM, ARTIST }

    // tabs para obtener album y canciones de un artista
    public static final String SONGS_TAB = "videos";
    public static final String ALBUMS_TAB = "playlists";

    public String build(String id, ContentType type) {
        return switch (type) {
            case SONG -> "https://music.youtube.com/watch?v=" + id;
            case ALBUM -> "https://music.youtube.com/playlist?list=" + id;
            case ARTIST -> "https://music.youtube.com/channel/" + id;
        };
    }

    public String resolveUrl(String idOrUrl, ContentType type) {
        if (idOrUrl.startsWith("http")) {
            return idOrUrl;
        }
        return build(idOrUrl, type);
    }

    public String buildArtistTabUrl(String artistId, String tabPath) {
        return "https://music.youtube.com/channel/" + artistId + "/" + tabPath;
    }
}
