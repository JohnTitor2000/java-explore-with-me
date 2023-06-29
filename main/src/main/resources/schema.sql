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
    created_on timestamp NOT NULL,
    eventDate timestamp NOT NULL,
    initiator_id BIGINT NOT NULL,
    paid boolean NOT NULL,
    participant_limit integer NOT NULL,
    published_on timestamp NOT NULL,
    request_moderation boolean NOT NULL,
    state VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS participation_request (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created timestamp NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(1300) NOT NULL,
);