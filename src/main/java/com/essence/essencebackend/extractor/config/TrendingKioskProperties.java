package com.essence.essencebackend.extractor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "extractor.youtube.kiosk")
public record TrendingKioskProperties(
        String songs,
        String albums,
        String artists
) {
    public TrendingKioskProperties {
        if (songs   == null) songs   = "trending_music";
        if (albums  == null) albums  = "New releases";
        if (artists == null) artists = "Trending";
    }

    public String getForType(com.essence.essencebackend.extractor.service.impl.TrendingType type) {
        return switch (type) {
            case SONGS   -> songs;
            case ALBUMS  -> albums;
            case ARTISTS -> artists;
        };
    }
}
