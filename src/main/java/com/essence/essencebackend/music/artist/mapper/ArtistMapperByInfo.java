package com.essence.essencebackend.music.artist.mapper;

import com.essence.essencebackend.music.artist.dto.ArtistResponseSimpleDTO;
import com.essence.essencebackend.music.artist.model.Artist;
import com.essence.essencebackend.music.shared.model.ContentType;
import com.essence.essencebackend.music.shared.service.ImageResolver;
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
    private final ImageResolver imageResolver;

    public Artist mapToArtist(ChannelInfo info, String artistUrl) {
        Artist artist = new Artist();
        artist.setNameArtist(cleanArtistName(info.getName()));
        artist.setDescription(info.getDescription());
        artist.setImageKey(imageResolver.resolve(info.getAvatars()));
        artist.setArtistUrl(urlExtractor.extractId(artistUrl, ContentType.ARTIST));
        artist.setNameNormalized(normalizeForSearch(cleanArtistName(info.getName())));
        return artist;
    }

    public Artist mapToArtistFromItem(ChannelInfoItem item) {
        Artist artist = new Artist();
        artist.setNameArtist(cleanArtistName(item.getName()));
        artist.setImageKey(imageResolver.resolve(item.getThumbnails()));
        artist.setArtistUrl(urlExtractor.extractId(item.getUrl(), ContentType.ARTIST));
        artist.setNameNormalized(normalizeForSearch(cleanArtistName(item.getName())));
        return artist;
    }

    public ArtistResponseSimpleDTO mapFromItem(ChannelInfoItem item) {
        return new ArtistResponseSimpleDTO(
                null,
                cleanArtistName(item.getName()),
                imageResolver.resolve(item.getThumbnails()),
                urlExtractor.extractId(item.getUrl(), ContentType.ARTIST)
        );
    }

    public String normalizeForSearch(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase().trim();
    }

    public String cleanArtistName(String name) {
        if (name == null) return null;
        return name.replaceAll("(?i)\\s*-\\s*Topic$", "").trim();
    }

    public boolean isTopic(String name) {
        return name != null && name.toLowerCase().endsWith("- topic");
    }
}