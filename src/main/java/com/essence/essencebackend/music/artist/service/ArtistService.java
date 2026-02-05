package com.essence.essencebackend.music.artist.service;

import com.essence.essencebackend.music.artist.model.Artist;

import java.util.List;

public interface ArtistService {
    List<Artist> getOrCreateArtistBySong(String artistUrl);
}
