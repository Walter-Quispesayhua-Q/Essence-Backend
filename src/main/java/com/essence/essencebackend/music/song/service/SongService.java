package com.essence.essencebackend.music.song.service;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.model.Song;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;

public interface SongService {
    SongResponseDTO getSongId(String songUrlOrId, String username, boolean forceRefresh);

    default SongResponseDTO getSongId(String songUrlOrId, String username) {
        return getSongId(songUrlOrId, username, false);
    }

    Song getOrCreateSong(String songUrlOrId);

    Song getOrCreateSongFromAlbum(StreamInfoItem item, Album album);
}
