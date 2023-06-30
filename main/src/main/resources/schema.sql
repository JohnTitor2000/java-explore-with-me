DROP TABLE users, categories;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    annotation VARCHAR(600) NOT NULL,
    description VARCHAR(1300) NOT NULL,
    category_id BIGINT NOT NULL,
    created_on timestamp NOT NULL,
    eventDate timestamp NOT NULL,
    initiator_id BIGINT NOT NULL,
    paid boolean NOT NULL,
    participant_limit integer NOT NULL,
    published_on timestamp NOT NULL,
    request_moderation boolean NOT NULL,
    state VARCHAR(20) NOT NULL,
    CONSTRAINT fk_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS participation_request (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created timestamp NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT unique_combination UNIQUE (event_id, requester_id)
);