package com.essence.essencebackend.library.playlist.controller;

import com.essence.essencebackend.library.playlist.dto.PlaylistRequestDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistResponseDTO;
import com.essence.essencebackend.library.playlist.dto.PlaylistSimpleResponseDTO;
import com.essence.essencebackend.library.playlist.service.PlaylistContentService;
import com.essence.essencebackend.library.playlist.service.PlaylistService;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.shared.dto.ResponseApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistContentService playlistContentService;

    // playlist crud
    @PostMapping
    public ResponseEntity<ResponseApi<PlaylistSimpleResponseDTO>> createPlaylist(@RequestBody PlaylistRequestDTO data,
                                                      @AuthenticationPrincipal Jwt jwt)
    {
        String username = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseApi<>("Se a creado exitosamente la Playlist", playlistService.createPlaylist(data, username)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseApi<PlaylistSimpleResponseDTO>> updatePlaylist(@PathVariable Long id
            ,@RequestBody PlaylistRequestDTO dataUpdate, @AuthenticationPrincipal Jwt jwt)
    {
        String username = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseApi<>("Se a actualizado correctamente la Playlist", playlistService.updatePlaylist(id, dataUpdate, username)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponseDTO> getPlaylist(@PathVariable Long id,
                                                           @AuthenticationPrincipal Jwt jwt)
    {
        String username = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistService.getPlaylist(id, username));
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<PlaylistSimpleResponseDTO> getForUpdate(@PathVariable Long id,
                                                                  @AuthenticationPrincipal Jwt jwt)
    {
        String username = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistService.getForUpdate(id, username));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id,
                                                  @AuthenticationPrincipal Jwt jwt)
    {
        String username = jwt.getSubject();
        playlistService.deletePlaylist(id, username);
        return ResponseEntity.noContent().build();
    }

    // Song content manager
    @GetMapping("/{id}/songs")
    public ResponseEntity<List<SongResponseSimpleDTO>> getListSong(@PathVariable Long id,
                                                                   @AuthenticationPrincipal Jwt jwt)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistContentService.getSongForPlaylist(id, jwt.getSubject()));
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Boolean> addSongToPlaylist(@PathVariable Long id, @PathVariable Long songId,
                                           @AuthenticationPrincipal Jwt jwt)
    {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistContentService.addSongToPlaylist(id, songId, jwt.getSubject()));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Void> deleteSongOfPlaylist(@PathVariable Long id, @PathVariable Long songId,
                                                     @AuthenticationPrincipal Jwt jwt)
    {
        playlistContentService.deleteSongToPlaylist(id, songId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
