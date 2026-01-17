CREATE TABLE IF NOT EXISTS songs (
    song_id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    duration_ms INTEGER NOT NULL CHECK (duration_ms > 0),
    release_date DATE,

    hls_master_key TEXT NOT NULL,
    cover_key TEXT,

    isrc CHAR(12) UNIQUE,

    album_id BIGINT REFERENCES albums(album_id),

    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING',
    total_plays BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );