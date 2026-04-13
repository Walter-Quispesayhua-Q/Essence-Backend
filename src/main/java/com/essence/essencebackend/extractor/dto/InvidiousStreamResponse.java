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
public class InvidiousStreamResponse {

    private String title;
    private String videoId;
    private String author;
    private String authorId;
    private String authorUrl;
    private String description;
    private long published;
    private long viewCount;
    private long likeCount;
    private int lengthSeconds;
    private List<VideoThumbnail> videoThumbnails;
    private List<AdaptiveFormat> adaptiveFormats;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoThumbnail {
        private String quality;
        private String url;
        private int width;
        private int height;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdaptiveFormat {
        private String url;
        private String itag;
        private String type;
        private String bitrate;
        private String container;
        private String encoding;
        private String audioQuality;
        private int audioSampleRate;
        private int audioChannels;
    }
}
