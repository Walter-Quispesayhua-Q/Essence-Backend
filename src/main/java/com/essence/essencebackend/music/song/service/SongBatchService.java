package com.essence.essencebackend.music.song.service;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.song.model.Song;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;

import java.util.List;

public interface SongBatchService {
    List<Song> saveSongsFromAlbum(Album album, List<StreamInfoItem> songItems);
}
