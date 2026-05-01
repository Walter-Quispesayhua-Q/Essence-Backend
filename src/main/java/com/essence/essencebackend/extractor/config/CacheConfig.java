package com.essence.essencebackend.extractor.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String TRENDING_SONGS = "trendingSongs";
    public static final String TRENDING_ALBUMS = "trendingAlbums";
    public static final String TRENDING_ARTISTS = "trendingArtists";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                TRENDING_SONGS, TRENDING_ALBUMS, TRENDING_ARTISTS
        );
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(30))
                .maximumSize(32));
        return manager;
    }
}
