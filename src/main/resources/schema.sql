CREATE TABLE IF NOT EXISTS task (
    task_id SERIAL PRIMARY KEY,
    title VARCHAR(100),
    description VARCHAR(100)
);