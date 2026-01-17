CREATE TABLE IF NOT EXISTS playlist_songs (
    playlist_id BIGINT NOT NULL REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    song_id BIGINT NOT NULL REFERENCES songs(song_id) ON DELETE CASCADE,
    added_at TIMESTAMPTZ DEFAULT NOW(),
    song_order INT NOT NULL DEFAULT 1,
    PRIMARY KEY (playlist_id, song_id)
);