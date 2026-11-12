CREATE TABLE IF NOT EXISTS venue (
    id        SERIAL PRIMARY KEY CHECK (id > 0),
    name      VARCHAR(100) NOT NULL CHECK (name <> ''),
    capacity  INT CHECK (capacity > 0),
    type      VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS event (
    id          SERIAL PRIMARY KEY CHECK (id > 0),
    name        VARCHAR(100) NOT NULL CHECK (name <> ''),
    event_date  TIMESTAMP NOT NULL,
    min_age     BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS location (
    id    BIGSERIAL PRIMARY KEY,
    x     BIGINT NOT NULL,
    y     INT NOT NULL,
    z     DOUBLE PRECISION NOT NULL,
    name  VARCHAR(255) NOT NULL CHECK (name <> '')
);

CREATE TABLE IF NOT EXISTS person (
    id           BIGSERIAL PRIMARY KEY,
    eye_color    VARCHAR(20),
    hair_color   VARCHAR(20),
    location_id  BIGINT REFERENCES location (id),
    height       DOUBLE PRECISION CHECK (height > 0),
    nationality  VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS ticket (
    id             BIGSERIAL PRIMARY KEY CHECK (id > 0),
    name           VARCHAR(100) NOT NULL CHECK (name <> ''),
    coord_x        BIGINT NOT NULL CHECK (coord_x > -626),
    coord_y        REAL NOT NULL,
    creation_date  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    person_id      BIGINT REFERENCES person (id),
    event_id       INT REFERENCES event (id),
    price          DOUBLE PRECISION NOT NULL CHECK (price > 0),
    type           VARCHAR(20),
    discount       INT NOT NULL CHECK (discount > 0 AND discount <= 100),
    number         DOUBLE PRECISION CHECK (number > 0),
    venue_id       INT REFERENCES venue (id)
);
