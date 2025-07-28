CREATE DATABASE tasks;

CREATE TABLE IF NOT EXISTS task
(
  id          BIGSERIAL PRIMARY KEY,
  title       VARCHAR(255),
  description TEXT,
  status      VARCHAR(20),
  due_date    DATE
);


