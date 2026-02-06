package com.essence.essencebackend.music.artist.service;

import com.essence.essencebackend.music.artist.model.Artist;

import java.util.List;
import java.util.Set;

public interface ArtistService {
    Set<Artist> getOrCreateArtistBySong(String artistUrl, String artists);
}
