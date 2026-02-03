package com.essence.essencebackend.extractor.service;


import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;

import java.util.List;

public interface MetadataExtractorTrendingService {
    List<SongResponseSimpleDTO> getTrendingSongs();
    List<AlbumResponseSimpleDTO> getTrendingAlbums();
    List<ArtistResponseSimpleDTO> getTrendingArtists();
}
