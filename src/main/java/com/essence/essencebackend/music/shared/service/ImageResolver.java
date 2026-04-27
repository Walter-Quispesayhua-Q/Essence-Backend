package com.essence.essencebackend.music.shared.service;

import org.schabi.newpipe.extractor.Image;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ImageResolver {

    private static final int HEIGHT_UNKNOWN = -1;

    public String resolve(List<Image> images) {
        if (images == null || images.isEmpty()) return null;

        String url = findByHeight(images);
        if (url != null) return enhanceUrl(url);

        String byResolution = findByResolutionLevel(images);
        if (byResolution != null) return enhanceUrl(byResolution);

        return enhanceUrl(images.getFirst().getUrl());
    }

    private String findByHeight(List<Image> images) {
        List<Image> withHeight = images.stream()
                .filter(img -> img.getHeight() != HEIGHT_UNKNOWN && img.getHeight() > 0)
                .toList();

        if (withHeight.isEmpty()) return null;

        return withHeight.stream()
                .max(Comparator.comparingInt(Image::getHeight))
                .map(Image::getUrl)
                .orElse(null);
    }

    private String findByResolutionLevel(List<Image> images) {
        for (Image.ResolutionLevel level : List.of(
                Image.ResolutionLevel.HIGH,
                Image.ResolutionLevel.MEDIUM)) {
            String url = images.stream()
                    .filter(img -> img.getEstimatedResolutionLevel() == level)
                    .findFirst()
                    .map(Image::getUrl)
                    .orElse(null);
            if (url != null) return url;
        }
        return null;
    }

    private String enhanceUrl(String url) {
        if (url == null) return null;
        return url.replaceAll("=w\\d+-h\\d+", "=w540-h540")
                  .replaceAll("=s\\d+", "=s540");
    }
}
