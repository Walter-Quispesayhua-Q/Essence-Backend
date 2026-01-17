CREATE TABLE IF NOT EXISTS song_likes (
    song_id BIGINT NOT NULL REFERENCES songs(song_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    liked_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (song_id, user_id)
);