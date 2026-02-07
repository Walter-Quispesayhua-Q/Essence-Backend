package com.essence.essencebackend.music.song.service;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.shared.dto.IdStreamingRequestDTO;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.model.Song;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;

public interface SongService {
    SongResponseDTO getSongId(IdStreamingRequestDTO data);

    Song getOrCreateSongFromAlbum(StreamInfoItem item, Album album);
}
