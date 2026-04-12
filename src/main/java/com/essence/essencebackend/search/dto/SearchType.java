package com.essence.essencebackend.search.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum SearchType {
    SONG("Canciones", "song", "cancion", "canción", "track", "musica", "música"),
    ALBUM("Álbumes", "album", "álbum", "playlist", "disco"),
    ARTIST("Artistas", "artist", "artista", "channel", "canal");

    private final String label;
    private final List<String> keywords;

    SearchType(String label, String... keywords) {
        this.label = label;
        this.keywords = List.of(keywords);
    }

    public static String detectFromQuery(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        String[] tokens = query.toLowerCase().trim().split("\\s+");
        Set<String> allKeywords = allKeywordsSet();
        for (String token : tokens) {
            if (!allKeywords.contains(token)) continue;
            for (SearchType type : values()) {
                if (type.keywords.contains(token)) {
                    return type.toNewPipeFilter();
                }
            }
        }
        return null;
    }

    public static String fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return SearchType.valueOf(value.toUpperCase()).toNewPipeFilter();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String cleanQuery(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }
        Set<String> allKeywords = allKeywordsSet();
        List<String> tokens = Arrays.stream(query.toLowerCase().trim().split("\\s+"))
                .filter(token -> !allKeywords.contains(token))
                .collect(Collectors.toList());
        return String.join(" ", tokens).trim();
    }

    private static Set<String> allKeywordsSet() {
        return Arrays.stream(values())
                .flatMap(type -> type.keywords.stream())
                .collect(Collectors.toSet());
    }

    public String toNewPipeFilter() {
        return switch (this) {
            case SONG   -> "music_songs";
            case ALBUM  -> "music_albums";
            case ARTIST -> "music_artists";
        };
    }
}