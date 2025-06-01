CREATE TABLE IF NOT EXISTS agenda (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    start_session   TIMESTAMP,
    end_session     TIMESTAMP
);