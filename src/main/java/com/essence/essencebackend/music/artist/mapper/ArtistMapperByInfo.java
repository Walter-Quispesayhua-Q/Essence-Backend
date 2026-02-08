package com.essence.essencebackend.music.artist.mapper;

import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.shared.service.UrlExtractor;
import lombok.RequiredArgsConstructor;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@RequiredArgsConstructor
@Service
public class ArtistMapperByInfo {

    private final UrlExtractor urlExtractor;

    public Artist mapToArtist(ChannelInfo info, String artistUrl) {
        Artist artist = new Artist();
        artist.setNameArtist(info.getName());
        artist.setDescription(info.getDescription());
        artist.setImageKey(info.getAvatars().isEmpty() ? null
                : info.getAvatars().get(0).getUrl());
        artist.setArtistUrl(urlExtractor.extractId(artistUrl, UrlExtractor.ContentType.ARTIST));
        artist.setNameNormalized(normalizeForSearch(info.getName()));
        return artist;
    }

    public Artist mapToArtistFromItem(ChannelInfoItem item) {
        Artist artist = new Artist();
        artist.setNameArtist(item.getName());
        artist.setImageKey(item.getThumbnails().isEmpty() ? null
                : item.getThumbnails().get(0).getUrl());
        artist.setArtistUrl(urlExtractor.extractId(item.getUrl(), UrlExtractor.ContentType.ARTIST));
        artist.setNameNormalized(normalizeForSearch(item.getName()));
        return artist;
    }

    public String normalizeForSearch(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase().trim();
    }
}
