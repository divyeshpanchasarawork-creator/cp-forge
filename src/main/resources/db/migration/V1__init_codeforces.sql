CREATE SCHEMA IF NOT EXISTS cp_forge;

CREATE TABLE IF NOT EXISTS cp_forge.codeforces_profile (
    id BIGSERIAL PRIMARY KEY,
    handle VARCHAR(50) NOT NULL UNIQUE,
    rating INTEGER,
    max_rating INTEGER,
    rank VARCHAR(50),
    max_rank VARCHAR(50),
    avatar_url TEXT,
    organization VARCHAR(255),
    country VARCHAR(100),
    last_synced_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cp_forge.codeforces_rating_history (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL REFERENCES cp_forge.codeforces_profile(id) ON DELETE CASCADE,
    contest_id INTEGER,
    contest_name TEXT,
    rank INTEGER,
    rating_update_time_seconds BIGINT,
    old_rating INTEGER,
    new_rating INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cf_rating_history_profile_id ON cp_forge.codeforces_rating_history(profile_id);
CREATE INDEX IF NOT EXISTS idx_cf_profile_handle ON cp_forge.codeforces_profile(handle);
