CREATE TABLE IF NOT EXISTS artists (
    artist_id BIGSERIAL PRIMARY KEY,
    name_artist TEXT NOT NULL,
    description TEXT,
    image_key TEXT,
    artist_url TEXT,
    country VARCHAR(10),
    total_streams BIGINT DEFAULT 0,
    name_normalized TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);