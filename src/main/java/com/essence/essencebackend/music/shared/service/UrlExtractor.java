package com.essence.essencebackend.music.shared.service;

import org.springframework.stereotype.Component;

@Component
public class UrlExtractor {
    public String extractId(String url, ContentType type) {
        return switch (type) {
            case SONG -> extractSongId(url);
            case ALBUM -> extractAlbumId(url);
            case ARTIST -> extractArtistId(url);
        };
    }

    public String resolverId(String urlOrId, ContentType type) {
        if (!urlOrId.startsWith("http")) {
            return urlOrId;
        }
        return extractId(urlOrId, type);
    }

    private String extractSongId(String url) {
        if (url.contains("v=")) {
            return url.split("v=")[1].split("&")[0];
        }
        return url;
    }
    private String extractAlbumId(String url) {
        if (url.contains("list=")) {
            return url.split("list=")[1].split("&")[0];
        }
        return url;
    }
    private String extractArtistId(String url) {
        if (url.contains("channel/")) {
            return url.split("channel/")[1].split("[?/]")[0];
        }
        return url;
    }
    public enum ContentType { SONG, ALBUM, ARTIST }
}