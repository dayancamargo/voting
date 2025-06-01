CREATE TABLE IF NOT EXISTS vote (
    id              SERIAL PRIMARY KEY,
    cpf             VARCHAR(11) NOT NULL,
    answer          VARCHAR(3) NOT NULL,
    agenda_id       SERIAL NOT NULL REFERENCES agenda(id)
);