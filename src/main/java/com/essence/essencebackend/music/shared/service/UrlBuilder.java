package com.essence.essencebackend.music.shared.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UrlBuilder {

    public enum ContentType { SONG, ALBUM, ARTIST }

    public String build(String id, ContentType type) {
        return switch (type) {
            case SONG -> "https://music.youtube.com/watch?v=" + id;
            case ALBUM -> "https://music.youtube.com/playlist?list=" + id;
            case ARTIST -> "https://music.youtube.com/channel/" + id;
        };
    }
}
