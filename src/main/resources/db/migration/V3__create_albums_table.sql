CREATE TABLE IF NOT EXISTS albums (
    album_id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    cover_key TEXT,
    release_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);