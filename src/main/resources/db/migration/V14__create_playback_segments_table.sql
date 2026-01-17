CREATE TABLE IF NOT EXISTS playback_segments (
    segment_id BIGSERIAL PRIMARY KEY,
    song_id BIGINT NOT NULL REFERENCES songs(song_id) ON DELETE CASCADE,
    segment_start_ms INT NOT NULL,
    segment_end_ms INT NOT NULL,
    play_count BIGINT DEFAULT 0,
    UNIQUE(song_id, segment_start_ms, segment_end_ms)
);