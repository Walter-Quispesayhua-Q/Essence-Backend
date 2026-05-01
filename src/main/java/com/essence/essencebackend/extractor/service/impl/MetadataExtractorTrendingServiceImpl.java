package com.essence.essencebackend.extractor.service.impl;

import com.essence.essencebackend.extractor.config.CacheConfig;
import com.essence.essencebackend.extractor.config.TrendingProperties;
import com.essence.essencebackend.extractor.service.MetadataExtractorTrendingService;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.mapper.AlbumMapper;
import com.essence.essencebackend.music.album.mapper.AlbumMapperByInfo;
import com.essence.essencebackend.music.album.repository.AlbumRepository;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.artist.mapper.ArtistMapper;
import com.essence.essencebackend.music.artist.mapper.ArtistMapperByInfo;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.mapper.SongMapperByInfo;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import com.essence.essencebackend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class MetadataExtractorTrendingServiceImpl implements MetadataExtractorTrendingService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final SongMapperByInfo songMapperByInfo;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumMapperByInfo albumMapperByInfo;
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final ArtistMapperByInfo artistMapperByInfo;
    private final SearchService searchService;
    private final TrendingProperties properties;

    @Override
    @Cacheable(value = CacheConfig.TRENDING_SONGS, unless = "#result == null || #result.isEmpty()")
    public List<SongResponseSimpleDTO> getTrendingSongs() {
        log.info("Obteniendo canciones mas escuchadas");
        List<SongResponseSimpleDTO> primary = searchSongs(properties.songsQuery());
        if (!primary.isEmpty()) return primary;

        List<SongResponseSimpleDTO> fallback = searchSongs(properties.fallbackQuery());
        if (!fallback.isEmpty()) return fallback;

        List<Song> songs = songRepository.findTop20ByOrderByTotalStreamsDesc();
        return songMapper.toListDto(songs).stream().limit(properties.limit()).toList();
    }

    @Override
    @Cacheable(value = CacheConfig.TRENDING_ALBUMS, unless = "#result == null || #result.isEmpty()")
    public List<AlbumResponseSimpleDTO> getTrendingAlbums() {
        log.info("Obteniendo los nuevos lanzamientos de album");
        List<AlbumResponseSimpleDTO> primary = searchAlbums(properties.albumsQuery());
        if (!primary.isEmpty()) return primary;

        List<AlbumResponseSimpleDTO> fallback = searchAlbums(properties.fallbackQuery());
        if (!fallback.isEmpty()) return fallback;

        return albumMapper.toListDto(albumRepository.findTop20WithArtistsByTotalStreams())
                .stream().limit(properties.limit()).toList();
    }

    @Override
    @Cacheable(value = CacheConfig.TRENDING_ARTISTS, unless = "#result == null || #result.isEmpty()")
    public List<ArtistResponseSimpleDTO> getTrendingArtists() {
        log.info("Obteniendo los artistas mas escuchados");
        List<ArtistResponseSimpleDTO> primary = searchArtists(properties.artistsQuery());
        if (!primary.isEmpty()) return primary;

        List<ArtistResponseSimpleDTO> fallback = searchArtists(properties.fallbackQuery());
        if (!fallback.isEmpty()) return fallback;

        return artistMapper.toListDto(artistRepository.findTop20ByOrderByTotalStreamsDesc())
                .stream().limit(properties.limit()).toList();
    }

    private List<SongResponseSimpleDTO> searchSongs(String query) {
        return searchService.searchByFilter(query, "music_songs", properties.limit())
                .stream()
                .filter(StreamInfoItem.class::isInstance)
                .map(StreamInfoItem.class::cast)
                .map(songMapperByInfo::mapFromItem)
                .toList();
    }

    private List<AlbumResponseSimpleDTO> searchAlbums(String query) {
        return searchService.searchByFilter(query, "music_albums", properties.limit())
                .stream()
                .filter(PlaylistInfoItem.class::isInstance)
                .map(PlaylistInfoItem.class::cast)
                .map(albumMapperByInfo::mapFromItem)
                .toList();
    }

    private List<ArtistResponseSimpleDTO> searchArtists(String query) {
        return searchService.searchByFilter(query, "music_artists", properties.limit())
                .stream()
                .filter(ChannelInfoItem.class::isInstance)
                .map(ChannelInfoItem.class::cast)
                .map(artistMapperByInfo::mapFromItem)
                .toList();
    }
}
