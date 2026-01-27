package com.essence.essencebackend.library.history.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.history.dto.PlayHistoryRequestDTO;
import com.essence.essencebackend.library.history.exception.AddToHistoryFailedException;
import com.essence.essencebackend.library.history.mapper.PlayHistoryMapper;
import com.essence.essencebackend.library.history.model.PlayHistory;
import com.essence.essencebackend.library.history.repository.PlayHistoryRepository;
import com.essence.essencebackend.library.history.service.PlayHistoryService;
import com.essence.essencebackend.library.playlist.exception.PlaylistNotFoundException;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.repository.PlaylistRepository;
import com.essence.essencebackend.music.album.exception.AlbumNotFoundException;
import com.essence.essencebackend.music.album.model.Album;
import com.essence.essencebackend.music.album.repository.AlbumRepository;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlayHistoryServiceImpl implements PlayHistoryService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final PlayHistoryMapper playHistoryMapper;
    private final PlaylistRepository playlistRepository;
    private final AlbumRepository albumRepository;
    private final SongMapper songMapper;

    @Override
    @Transactional
    public void addSongToHistory(
            Long id, String username, PlayHistoryRequestDTO data) {
        log.info("Agregando canción: {}, al historial del usuario: {}", id, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundForUsernameException(username)
                );
        Song song = songRepository.findById(id).orElseThrow(
                () -> new SongNotFoundException(id)
        );

        Playlist playlist = null;
        if (data.playlistId() != null) {
            playlist = playlistRepository.findById(data.playlistId())
                    .orElseThrow(
                            () -> new PlaylistNotFoundException(data.playlistId())
                    );
        }

        Album album = null;
        if (data.albumId() != null) {
            album = albumRepository.findById(data.albumId())
                    .orElseThrow(
                            () -> new AlbumNotFoundException(data.albumId())
                    );
        }

        PlayHistory playHistory = playHistoryMapper.toEntity(data);
        playHistory.setUser(user);
        playHistory.setSong(song);
        playHistory.setPlaylist(playlist);
        playHistory.setAlbum(album);

        try {
            playHistoryRepository.save(playHistory);
        } catch (RuntimeException e) {
            throw new AddToHistoryFailedException(id, username, e);
        }
    }

    @Override
    public List<SongResponseSimpleDTO> getSongOfHistory(String username) {
        log.info("Obteniendo el historial de canciones del usuario: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundForUsernameException(username)
                );

        List<PlayHistory> historyList = playHistoryRepository.findRecentUniqueSongsByUserId(user.getId());

        if (historyList.isEmpty()) {
            return List.of();
        }

        List<Song> songs = historyList.stream()
                .map(PlayHistory::getSong)
                .toList();

        return songMapper.toListDto(songs);
    }

}
