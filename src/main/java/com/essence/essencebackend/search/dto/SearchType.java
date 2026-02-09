package com.essence.essencebackend.search.dto;

import lombok.Getter;

import java.util.List;

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

    // Desde query → retorna filtro directo
    public static String detectFromQuery(String query) {
        String lower = query.toLowerCase();
        for (SearchType type : values()) {
            for (String keyword : type.keywords) {
                if (lower.contains(keyword)) {
                    return type.toNewPipeFilter();
                }
            }
        }
        return null;
    }
    // Desde categoría → retorna filtro directo
    public static String fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            SearchType type = SearchType.valueOf(value.toUpperCase());
            return type.toNewPipeFilter();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Limpiar query de keywords
    public static String cleanQuery(String query) {
        String clean = query.toLowerCase();
        for (SearchType type : values()) {
            for (String keyword : type.keywords) {
                clean = clean.replace(keyword, "");
            }
        }
        return clean.trim().replaceAll("\\s+", " ");
    }

    public String toNewPipeFilter() {
        return switch (this) {
            case SONG -> "videos";
            case ALBUM -> "playlists";
            case ARTIST -> "channels";
        };
    }
}