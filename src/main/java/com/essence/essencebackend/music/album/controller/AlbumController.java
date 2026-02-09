package com.essence.essencebackend.music.album.controller;

import com.essence.essencebackend.library.like.service.LikeService;
import com.essence.essencebackend.music.album.dto.AlbumResponseDTO;
import com.essence.essencebackend.music.album.service.AlbumService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService albumService;
    private final LikeService<Long> likeService;

    public AlbumController(
            AlbumService albumService,
            @Qualifier("albumLikeServiceImpl") LikeService<Long> likeService
    ) {
        this.albumService = albumService;
        this.likeService = likeService;
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumResponseDTO> getAlbum(
            @PathVariable String albumId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                albumService.getAlbumDetail(jwt.getSubject(), albumId)
        );
    }

    @PostMapping("/{albumId}/like")
    public ResponseEntity<Void> addLikeAlbum(
            @PathVariable Long albumId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        likeService.addLike(albumId, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{albumId}/like")
    public ResponseEntity<Void> deleteLikeAlbum(
            @PathVariable Long albumId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        likeService.deleteLike(albumId, jwt.getSubject());
        return ResponseEntity.ok().build();
    }
}
