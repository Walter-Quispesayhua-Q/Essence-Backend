CREATE TABLE IF NOT EXISTS album_likes (
    album_id BIGINT NOT NULL REFERENCES albums(album_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    liked_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (album_id, user_id)
);