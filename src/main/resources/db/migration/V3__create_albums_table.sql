CREATE TABLE IF NOT EXISTS albums (
    album_id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    release_date DATE
);