-- liquibase formatted sql

-- changeset terzi:1
CREATE TABLE IF NOT EXISTS faculty (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(50) NOT NULL
);

-- changeset terzi:2
CREATE TABLE IF NOT EXISTS student (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER DEFAULT 20 CHECK (age >= 16),
    faculty_id INTEGER REFERENCES faculty(id)
);

-- changeset terzi:3
CREATE INDEX IF NOT EXISTS idx_student_name
    ON student(name);

-- changeset terzi:4
CREATE INDEX IF NOT EXISTS idx_faculty_name_color
    ON faculty(name, color);