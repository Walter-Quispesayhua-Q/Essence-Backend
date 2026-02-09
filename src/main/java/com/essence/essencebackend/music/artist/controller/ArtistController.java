package com.essence.essencebackend.music.artist.controller;

import com.essence.essencebackend.library.like.service.LikeService;
import com.essence.essencebackend.music.artist.dto.ArtistsResponseDTO;
import com.essence.essencebackend.music.artist.service.ArtistService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artist")
public class ArtistController {

    private final ArtistService artistService;
    private final LikeService<Long> likeService;

    public ArtistController(
            ArtistService artistService,
            @Qualifier("artistLikeServiceImpl") LikeService<Long> likeService
    ) {
        this.artistService = artistService;
        this.likeService = likeService;
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistsResponseDTO> getArtist(
            @PathVariable String artistId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                artistService.getArtistDetail(jwt.getSubject(), artistId)
        );
    }

    @PostMapping("/{artistId}/like")
    public ResponseEntity<Void> addLikeArtist(
            @PathVariable Long artistId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        likeService.addLike(artistId, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{artistId}/like")
    public ResponseEntity<Void> deleteLikeArtist(
            @PathVariable Long artistId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        likeService.deleteLike(artistId, jwt.getSubject());
        return ResponseEntity.ok().build();
    }
}
