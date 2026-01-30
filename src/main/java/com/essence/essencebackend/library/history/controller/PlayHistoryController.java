package com.essence.essencebackend.library.history.controller;

import com.essence.essencebackend.library.history.dto.PlayHistoryRequestDTO;
import com.essence.essencebackend.library.history.service.PlayHistoryService;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/history")
public class PlayHistoryController {

    private final PlayHistoryService playHistoryService;

    @PostMapping("/songs/{songId}")
    public ResponseEntity<Void> addSongHistory(@PathVariable Long songId,
                                               @RequestBody PlayHistoryRequestDTO data,
                                               @AuthenticationPrincipal UserDetails userDetails)
    {
        playHistoryService.addSongToHistory(songId, userDetails.getUsername(), data);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<SongResponseSimpleDTO>> getSongsOfHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playHistoryService.getSongOfHistory(userDetails.getUsername()));
    }
}
