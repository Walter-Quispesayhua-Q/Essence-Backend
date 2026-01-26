package com.essence.essencebackend.library.history.dto;

public record PlayHistoryRequestDTO(
        Integer durationListenedMs,
        Boolean completed,
        Boolean skipped,
        Integer skiPositionsMs,
        String deviceType
) {
}
