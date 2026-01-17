CREATE TABLE IF NOT EXISTS album_artists (
    album_id BIGINT NOT NULL REFERENCES albums(album_id) ON DELETE CASCADE,
    artist_id BIGINT NOT NULL REFERENCES artists(artist_id) ON DELETE CASCADE,
    is_primary BOOLEAN NOT NULL DEFAULT false,
    artist_order INT NOT NULL DEFAULT 1,
    PRIMARY KEY (album_id, artist_id)
);
