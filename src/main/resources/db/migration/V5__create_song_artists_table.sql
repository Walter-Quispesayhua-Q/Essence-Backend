CREATE TABLE IF NOT EXISTS song_artists (
    song_id BIGINT NOT NULL REFERENCES songs(song_id) ON DELETE CASCADE,
    artist_id BIGINT NOT NULL REFERENCES artists(artist_id) ON DELETE CASCADE,
    is_primary BOOLEAN NOT NULL DEFAULT false,
    artist_order INT NOT NULL DEFAULT 1,
    PRIMARY KEY (song_id, artist_id)
);