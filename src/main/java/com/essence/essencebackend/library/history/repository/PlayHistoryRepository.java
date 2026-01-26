package com.essence.essencebackend.library.history.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.library.history.model.PlayHistory;
import com.essence.essencebackend.music.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    PlayHistory findByUserIdAndSongId(Long userId, Long songId);
}
