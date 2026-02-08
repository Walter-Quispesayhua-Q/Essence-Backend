package com.essence.essencebackend.music.album.service;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.artist.model.Artist;

import java.util.Set;

public interface AlbumOfSongService {
    Album getOrCreateAlbumBySong(String songName, String artistName, Set<Artist> artists);

}
