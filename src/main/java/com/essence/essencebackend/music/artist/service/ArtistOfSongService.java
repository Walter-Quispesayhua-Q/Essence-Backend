package com.essence.essencebackend.music.artist.service;

import com.essence.essencebackend.music.artist.model.Artist;

import java.util.Set;

public interface ArtistOfSongService {
    Set<Artist> getOrCreateArtistBySong(String artistUrl, String artists);
}
