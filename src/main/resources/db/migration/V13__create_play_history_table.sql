CREATE TABLE IF NOT EXISTS play_history (
    history_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    song_id BIGINT NOT NULL REFERENCES songs(song_id) ON DELETE CASCADE,
    played_at TIMESTAMPTZ DEFAULT NOW(),
    duration_listened_ms INT DEFAULT 0,
    completed BOOLEAN DEFAULT false,
    skipped BOOLEAN DEFAULT false,
    skip_position_ms INT,
    device_type VARCHAR(50)
);
