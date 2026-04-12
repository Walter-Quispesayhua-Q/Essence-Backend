package com.essence.essencebackend.extractor.service.impl;

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
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import com.essence.essencebackend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class MetadataExtractorTrendingServiceImpl implements MetadataExtractorTrendingService {

    private static final int TRENDING_LIMIT = 15;
    private static final int SEARCH_FALLBACK_LIMIT = 10;

    private final ExtractorTrendingService extractorTrendingService;
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final SearchService searchService;
    private final AlbumMapperByInfo albumMapperByInfo;
    private final ArtistMapperByInfo artistMapperByInfo;

    @Override
    public List<SongResponseSimpleDTO> getTrendingSongs() {
        log.info("Obteniendo canciones mas escuchadas");
        try {
            List<SongResponseSimpleDTO> songsYoutubeMusic =
                    extractorTrendingService.getTrending(TrendingType.SONGS, this::toDtoSong);
            if (!songsYoutubeMusic.isEmpty()) {
                return songsYoutubeMusic.stream().limit(TRENDING_LIMIT).toList();
            }
        } catch (Exception e) {
            log.warn("Trending songs no disponible: {}", e.getMessage());
        }
        List<Song> songs = songRepository.findTop20ByOrderByTotalStreamsDesc();
        return songMapper.toListDto(songs);
    }

    @Override
    public List<AlbumResponseSimpleDTO> getTrendingAlbums() {
        log.info("Obteniendo los nuevos lanzamientos de album");
        try {
            List<AlbumResponseSimpleDTO> albumYoutubeMusic =
                    extractorTrendingService.getTrending(TrendingType.ALBUMS, this::toDtoAlbum);
            if (!albumYoutubeMusic.isEmpty()) return albumYoutubeMusic;
        } catch (Exception e) {
            log.warn("Trending albums no disponible: {}", e.getMessage());
        }
        List<InfoItem> searchItems = searchService.searchByFilter("top latin music", "music_albums", SEARCH_FALLBACK_LIMIT);
        List<AlbumResponseSimpleDTO> searchAlbums = searchItems.stream()
                .filter(PlaylistInfoItem.class::isInstance)
                .map(PlaylistInfoItem.class::cast)
                .map(albumMapperByInfo::mapFromItem)
                .toList();
        if (!searchAlbums.isEmpty()) return searchAlbums;
        return albumMapper.toListDto(albumRepository.findTop20WithArtistsByTotalStreams());
    }

    @Override
    public List<ArtistResponseSimpleDTO> getTrendingArtists() {
        log.info("Obteniendo los artistas mas escuchados");
        try {
            List<ArtistResponseSimpleDTO> artistYoutubeMusic =
                    extractorTrendingService.getTrending(TrendingType.ARTISTS, this::toDtoArtist);
            if (!artistYoutubeMusic.isEmpty()) return artistYoutubeMusic;
        } catch (Exception e) {
            log.warn("Trending artists no disponible: {}", e.getMessage());
        }
        List<InfoItem> searchItems = searchService.searchByFilter("top latin music", "music_artists", SEARCH_FALLBACK_LIMIT);
        List<ArtistResponseSimpleDTO> searchArtists = searchItems.stream()
                .filter(ChannelInfoItem.class::isInstance)
                .map(ChannelInfoItem.class::cast)
                .map(artistMapperByInfo::mapFromItem)
                .toList();
        if (!searchArtists.isEmpty()) return searchArtists;
        return artistMapper.toListDto(artistRepository.findTop20ByOrderByTotalStreamsDesc());
    }

    private String extractId(String url) {
        if (url.contains("v=")) {
            return url.split("v=")[1].split("&")[0];
        }
        if (url.contains("list=")) {
            return url.split("list=")[1].split("&")[0];
        }
        if (url.contains("channel/")) {
            return url.split("channel/")[1].split("[?/]")[0];
        }
        return url;
    }

    private SongResponseSimpleDTO toDtoSong(StreamInfoItem item) {
        long viewCount = item.getViewCount();
        return new SongResponseSimpleDTO(
                null,
                item.getName(),
                (int) (item.getDuration() * 1000),
                extractId(item.getUrl()),
                item.getThumbnails().isEmpty() ? null
                        : item.getThumbnails().get(0).getUrl(),
                "MUSIC",
                viewCount >= 0 ? viewCount : null,
                item.getUploaderName(),
                null,
                item.getUploadDate() != null
                        ? item.getUploadDate().offsetDateTime().toLocalDate()
                        : null
        );
    }

    private AlbumResponseSimpleDTO toDtoAlbum(PlaylistInfoItem item) {
        return new AlbumResponseSimpleDTO(
                null,
                item.getName(),
                item.getThumbnails().isEmpty() ? null
                        : item.getThumbnails().get(0).getUrl(),
                extractId(item.getUrl()),
                List.of(item.getUploaderName()),
                null
        );
    }

    private ArtistResponseSimpleDTO toDtoArtist(ChannelInfoItem item) {
        return new ArtistResponseSimpleDTO(
                null,
                item.getName(),
                item.getThumbnails().isEmpty() ? null
                        : item.getThumbnails().get(0).getUrl(),
                extractId(item.getUrl())
        );
    }
}
