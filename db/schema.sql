CREATE TABLE jobs(
    id UUID PRIMARY KEY,
    type text NOT NULL,
    payload JSONB,
    status TEXT NOT NULL CHECK(status IN ('QUEUED', 'RUNNING', 'SUCCESS', 'FAILED')),
    attempt INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW(),
    started_at TIMESTAMP,
    finished_at TIMESTAMP,

    result JSONB,
    error TEXT
);