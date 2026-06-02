CREATE SCHEMA IF NOT EXISTS finance_db;

SET search_path TO finance_db;

CREATE TABLE IF NOT EXISTS users (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id          BIGSERIAL      PRIMARY KEY,
    description VARCHAR(255)   NOT NULL,
    amount      NUMERIC(15, 2) NOT NULL,
    type        VARCHAR(50)    NOT NULL,
    date        DATE           NOT NULL,
    category_id BIGINT         NOT NULL REFERENCES categories(id),
    user_id     BIGINT         NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP      NOT NULL
);
