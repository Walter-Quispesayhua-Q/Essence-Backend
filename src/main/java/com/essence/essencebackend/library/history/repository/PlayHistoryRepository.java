package com.essence.essencebackend.library.history.repository;

import com.essence.essencebackend.library.history.model.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
}
