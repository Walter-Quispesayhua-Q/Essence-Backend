package com.essence.essencebackend.music.album.mapper;

import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumMapperByInfo {

    private final UrlExtractor urlExtractor;

    public Album mapToAlbum(PlaylistInfo info) {
        Album album = new Album();
        album.setTitle(info.getName());
        album.setDescription(info.getDescription().getContent());
        album.setImageKey(info.getThumbnails().isEmpty() ? null
                : info.getThumbnails().get(0).getUrl());
        album.setAlbumUrl(
                urlExtractor.extractId(info.getUrl(), UrlExtractor.ContentType.ALBUM)
        );
        return album;
    }
}
