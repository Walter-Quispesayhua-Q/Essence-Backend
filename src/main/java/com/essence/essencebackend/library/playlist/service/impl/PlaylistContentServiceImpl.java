package com.essence.essencebackend.library.playlist.service.impl;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.playlist.exception.PlaylistNotFoundException;
import com.essence.essencebackend.library.playlist.exception.SongAlreadyInPlaylistException;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
import com.essence.essencebackend.library.playlist.model.Playlist;
import com.essence.essencebackend.library.playlist.model.PlaylistSong;
import com.essence.essencebackend.library.playlist.model.embedded.PlaylistSongId;
import com.essence.essencebackend.library.playlist.repository.PlaylistRepository;
import com.essence.essencebackend.library.playlist.repository.PlaylistSongRepository;
import com.essence.essencebackend.library.playlist.service.PlaylistContentService;
import com.essence.essencebackend.music.song.dto.SongResponseSimpleDTO;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import com.essence.essencebackend.music.song.mapper.SongMapper;
import com.essence.essencebackend.music.song.model.Song;
import com.essence.essencebackend.music.song.repository.SongRepository;
import jakarta.transaction.Transactional;
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
    private final SongRepository songRepository;

    @Override
    @Transactional
    public boolean addSongToPlaylist(Long songId, Long playlistId, String username) {
        log.info("Agregando canción con el id: {} , a la playlists con el id: {} , por el usuario: {}" , songId, playlistId, username);

        PlaylistSongContext context = getContext(username, playlistId, songId);

        PlaylistSongId playlistSongId = new PlaylistSongId(playlistId, songId);

        if (playlistSongRepository.existsById(playlistSongId)) {
            throw new SongAlreadyInPlaylistException(songId, playlistId);
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setId(playlistSongId);
        playlistSong.setPlaylist(context.playlist);
        playlistSong.setSong(context.song);
        playlistSong.setAddedAt(Instant.now());
        playlistSong.setSongOrder(context.playlist.getPlaylistSongs().size() + 1);

        playlistSongRepository.save(playlistSong);

        return true;
    }

    @Override
    @Transactional
    public void deleteSongToPlaylist(Long songId, Long playlistId, String username) {
        log.info("Eliminando canción con el id: {} , de la playlists con el id: {} , por el usuario: {}" , songId, playlistId, username);

        getContext(username, playlistId, songId);

        PlaylistSongId playlistSongId = new PlaylistSongId(playlistId, songId);

        PlaylistSong playlistSong = playlistSongRepository.findById(playlistSongId).orElseThrow(
                () -> new RuntimeException("No se encontró la cancion en la playlist")
        );

        playlistSongRepository.delete(playlistSong);
    }

    @Override
    public List<SongResponseSimpleDTO> getSongForPlaylist(Long id, String username) {
        log.info("Obteniendo canciones para la playlist: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(id, user).orElseThrow(
                () -> new PlaylistNotFoundException(id)
        );

        List<PlaylistSong> playlistSong = playlistSongRepository.findByPlaylistPlaylistId(playlist.getPlaylistId());

        return songMapper.toListDto(
                playlistSong.stream()
                        .map(PlaylistSong::getSong)
                        .toList()
        );
    }

    private record PlaylistSongContext(Playlist playlist, Song song){}

    private PlaylistSongContext getContext(String username, Long playlistId, Long songId) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        Playlist playlist = playlistRepository.findByPlaylistIdAndUser(playlistId, user).orElseThrow(
                () -> new PlaylistNotFoundException(playlistId)
        );

        Song song = songRepository.findById(songId).orElseThrow(
                () -> new SongNotFoundException(songId)
        );

        return new PlaylistSongContext(playlist, song);
    }
}
