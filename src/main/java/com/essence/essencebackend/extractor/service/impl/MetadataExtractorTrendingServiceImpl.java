package com.essence.essencebackend.extractor.service.impl;

import com.essence.essencebackend.extractor.service.MetadataExtractorTrendingService;
import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.mapper.AlbumMapper;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.repository.AlbumRepository;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.artist.mapper.ArtistMapper;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.artist.repository.ArtistRepository;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class MetadataExtractorTrendingServiceImpl implements MetadataExtractorTrendingService {

    private final ExtractorTrendingService extractorTrendingService;
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Override
    public List<SongResponseSimpleDTO> getTrendingSongs() {
        log.info("Obteniendo canciones mas escuchadas");
        List<SongResponseSimpleDTO> songsYoutubeMusic = extractorTrendingService.getTrending(TrendingType.SONGS, this::toDtoSong);
        if (songsYoutubeMusic.isEmpty()) {
            List<Song> songs = songRepository.findTop20ByOrderByTotalStreamsDesc();
            return songMapper.toListDto(songs);
        }
        return songsYoutubeMusic;
    }

    @Override
    public List<AlbumResponseSimpleDTO> getTrendingAlbums() {
        log.info("Obteniendo los nuevos lanzamientos de album");
        List<AlbumResponseSimpleDTO> albumYoutubeMusic = extractorTrendingService.getTrending(TrendingType.ALBUMS, this::toDtoAlbum);
        if (albumYoutubeMusic.isEmpty()) {
            List<Album> albums = albumRepository.findTop20ByOrderByTotalStreamsDesc();
            return albumMapper.toListDto(albums);
        }
        return albumYoutubeMusic;
    }

    @Override
    public List<ArtistResponseSimpleDTO> getTrendingArtists() {
        log.info("Obteniendo los artistas mas escuchados");
        List<ArtistResponseSimpleDTO> artistYoutubeMusic =
                extractorTrendingService.getTrending(TrendingType.ARTISTS, this::toDtoArtist);
        if (artistYoutubeMusic.isEmpty()) {
            List<Artist> artists = artistRepository.findTop20ByOrderByTotalStreamsDesc();
            return artistMapper.toListDto(artists);
        }
        return artistYoutubeMusic;
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
        return new SongResponseSimpleDTO(
                null,
                item.getName(),
                (int) (item.getDuration() * 1000),
                extractId(item.getUrl()),
                item.getThumbnails().isEmpty() ? null
                        : item.getThumbnails().get(0).getUrl(),
                null,
                item.getViewCount(),
                item.getUploaderName(),
                null,
                item.getUploadDate() != null
                        ? LocalDate.parse(item.getUploadDate().offsetDateTime()
                        .toLocalDate().toString())
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
