package com.essence.essencebackend.music.artist.service;

import com.essence.essencebackend.music.artist.dto.ArtistsResponseDTO;

public interface ArtistService {
    ArtistsResponseDTO getArtistDetail(String username, String artistUrlOrId);
}
