package com.essence.essencebackend.library.playlist.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.playlist.exception.PlaylistNotFoundException;
import com.essence.essencebackend.library.playlist.exception.SongAlreadyInPlaylistException;
import com.essence.essencebackend.library.playlist.exception.SongNotInPlaylistException;
import com.essence.essencebackend.autentication.shared.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.model.PlaylistSong;
import com.essence.essencebackend.library.playlist.model.embedded.PlaylistSongId;
import com.essence.essencebackend.library.playlist.repository.PlaylistRepository;
import com.essence.essencebackend.library.playlist.repository.PlaylistSongRepository;
import com.essence.essencebackend.library.playlist.service.PlaylistContentService;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.service.SongService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlaylistContentServiceImpl implements PlaylistContentService {

    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongMapper songMapper;
    private final SongService songService;

    @Override
    @Transactional
    public boolean addSongToPlaylist(Long playlistId, String songKey, String username) {
        log.info("Agregando canción con key: {} , a la playlist: {} , por el usuario: {}" , songKey, playlistId, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(playlistId, user).orElseThrow(
                () -> new PlaylistNotFoundException(playlistId)
        );

        Song song = songService.getOrCreateSong(songKey);

        PlaylistSongId playlistSongId = new PlaylistSongId(playlistId, song.getId());

        if (playlistSongRepository.existsById(playlistSongId)) {
            throw new SongAlreadyInPlaylistException(song.getId(), playlistId);
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setId(playlistSongId);
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlistSong.setAddedAt(Instant.now());
        playlistSong.setSongOrder((int) playlistSongRepository.countByPlaylistPlaylistId(playlistId) + 1);

        playlistSongRepository.save(playlistSong);

        return true;
    }

    @Override
    @Transactional
    public void deleteSongToPlaylist(Long playlistId, Long songId, String username) {
        log.info("Eliminando canción con el UrlId: {} , de la playlists con el UrlId: {} , por el usuario: {}" , songId, playlistId, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        playlistRepository.findByPlaylistIdAndUser(playlistId, user).orElseThrow(
                () -> new PlaylistNotFoundException(playlistId)
        );

        PlaylistSongId playlistSongId = new PlaylistSongId(playlistId, songId);

        PlaylistSong playlistSong = playlistSongRepository.findById(playlistSongId).orElseThrow(
                () -> new SongNotInPlaylistException(songId, playlistId)
        );

        playlistSongRepository.delete(playlistSong);
    }

    @Override
    public List<SongResponseSimpleDTO> getSongForPlaylist(Long id, String username) {
        log.info("Obteniendo canciones para la playlist: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findAccessiblePlaylist(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );

        List<PlaylistSong> playlistSong = playlistSongRepository.findByPlaylistPlaylistId(playlist.getPlaylistId());

        return songMapper.toListDto(
                playlistSong.stream()
                        .map(PlaylistSong::getSong)
                        .toList()
        );
    }
}

