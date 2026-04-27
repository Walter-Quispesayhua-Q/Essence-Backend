package com.essence.essencebackend.music.album.mapper;

import com.essence.essencebackend.music.album.dto.AlbumResponseSimpleDTO;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.service.ImageResolver;
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
    private final ImageResolver imageResolver;

    public Album mapToAlbum(PlaylistInfo info) {
        Album album = new Album();
        album.setTitle(info.getName());
        album.setDescription(info.getDescription().getContent());
        album.setImageKey(imageResolver.resolve(info.getThumbnails()));
        album.setAlbumUrl(
                urlExtractor.extractId(info.getUrl(), ContentType.ALBUM)
        );
        return album;
    }

    public AlbumResponseSimpleDTO mapFromItem(PlaylistInfoItem item) {
        return new AlbumResponseSimpleDTO(
                null,
                item.getName(),
                imageResolver.resolve(item.getThumbnails()),
                urlExtractor.extractId(item.getUrl(), ContentType.ALBUM),
                List.of(item.getUploaderName()),
                null
        );
    }
}
