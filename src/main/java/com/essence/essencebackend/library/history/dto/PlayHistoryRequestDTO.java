package com.essence.essencebackend.library.history.dto;

public record PlayHistoryRequestDTO(
        Long playlistId,
        Long albumId,
        Integer durationListenedMs,
        Boolean completed,
        Boolean skipped,
        Integer skipPositionMs,
        String deviceType
) {
}
