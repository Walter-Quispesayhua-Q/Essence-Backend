package com.essence.essencebackend.extractor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "extractor.youtube.trending")
public record TrendingProperties(
        String songsQuery,
        String albumsQuery,
        String artistsQuery,
        Integer limit,
        String fallbackQuery
) {
    public TrendingProperties {
        if (songsQuery    == null) songsQuery    = "top hits";
        if (albumsQuery   == null) albumsQuery   = "new releases";
        if (artistsQuery  == null) artistsQuery  = "popular artists";
        if (limit         == null) limit         = 15;
        if (fallbackQuery == null) fallbackQuery = "top latin music";
    }
}
