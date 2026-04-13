package com.essence.essencebackend.extractor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PipedStreamResponse {

    private String title;
    private String description;
    private String uploader;
    private String uploaderUrl;
    private String uploaderAvatar;
    private String thumbnailUrl;
    private int duration;
    private long views;
    private long likes;
    private long dislikes;
    private String uploadDate;
    private boolean livestream;
    private String hls;
    private String dash;
    private String proxyUrl;
    private List<AudioStream> audioStreams;
    private List<RelatedStream> relatedStreams;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AudioStream {
        private String url;
        private String format;
        private String quality;
        private String mimeType;
        private String codec;
        private int bitrate;
        private boolean videoOnly;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RelatedStream {
        private String url;
        private String title;
        private String thumbnail;
        private String uploader;
        private String uploaderUrl;
        private String uploaderAvatar;
        private String uploadedDate;
        private int duration;
        private long views;
        private boolean uploaderVerified;
    }
}
