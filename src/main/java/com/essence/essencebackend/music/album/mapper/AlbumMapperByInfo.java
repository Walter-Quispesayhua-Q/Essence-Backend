package com.essence.essencebackend.music.album.mapper;

import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public AlbumResponseSimpleDTO mapFromItem(PlaylistInfoItem item) {
        return new AlbumResponseSimpleDTO(
                null,
                item.getName(),
                item.getThumbnails().isEmpty() ? null
                        : item.getThumbnails().get(0).getUrl(),
                urlExtractor.extractId(item.getUrl(), UrlExtractor.ContentType.ALBUM),
                List.of(item.getUploaderName()),
                null
        );
    }
}
