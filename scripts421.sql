CREATE TABLE faculty (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(50) NOT NULL,
    CONSTRAINT unique_faculty_name_color UNIQUE (name, color)
);

CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    age INTEGER DEFAULT 20 CHECK (age >= 16),
    faculty_id INTEGER REFERENCES faculty(id) ON DELETE SET NULL
);