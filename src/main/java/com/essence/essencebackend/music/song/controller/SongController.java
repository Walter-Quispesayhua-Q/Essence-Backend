package com.essence.essencebackend.music.song.controller;

import com.essence.essencebackend.library.like.service.LikeService;
import com.essence.essencebackend.music.song.dto.SongResponseDTO;
import com.essence.essencebackend.music.song.dto.SongSyncRequestDTO;
import com.essence.essencebackend.music.song.service.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/song")
public class SongController {

    private final SongService songService;
    private final LikeService<Long> likeService;
    public SongController(
            SongService songService,
            @Qualifier("songLikeServiceImpl") LikeService<Long> likeService
    ) {
        this.songService = songService;
        this.likeService = likeService;
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongResponseDTO> getSong(
            @PathVariable String songId,
            @RequestParam(defaultValue = "false") boolean forceRefresh,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                songService.getSongId(songId, jwt.getSubject(), forceRefresh)
        );
    }

    @PostMapping("/sync")
    public ResponseEntity<SongResponseDTO> syncSongFromClient(
            @Valid @RequestBody SongSyncRequestDTO request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                songService.syncSongFromClient(request, jwt.getSubject())
        );
    }

    @PatchMapping("/{videoId}/streaming-url")
    public ResponseEntity<SongResponseDTO> refreshStreamingUrl(
            @PathVariable String videoId,
            @RequestParam String streamingUrl,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                songService.refreshStreamingUrl(videoId, streamingUrl, jwt.getSubject())
        );
    }

    @PostMapping("/{songId}/like")
    public ResponseEntity<Void> addlikeSong(
            @PathVariable Long songId,
            @AuthenticationPrincipal Jwt jwt
            ) {
        likeService.addLike(songId, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{songId}/like")
    public ResponseEntity<Void> deleteLikeSong(
            @PathVariable Long songId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        likeService.deleteLike(songId, jwt.getSubject());
        return ResponseEntity.ok().build();
    }
}
