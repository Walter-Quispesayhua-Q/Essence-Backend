package com.essence.essencebackend.music.album.service;

import com.essence.essencebackend.music.album.model.Album;

public interface AlbumService {
    Album getOrCreateAlbumBySong(String songName, String artistName);

}
