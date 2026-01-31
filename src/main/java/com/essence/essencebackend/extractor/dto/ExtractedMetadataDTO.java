package com.essence.essencebackend.extractor.dto;

public record ExtractedMetadataDTO(
        ExtractedSongDTO song,
        ExtractedAlbumDTO album,
        ExtractedArtistDTO artist
) {
}
