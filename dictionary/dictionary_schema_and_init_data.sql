-- Create the database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS ds_dictionary;
USE ds_dictionary;

-- Create the dictionary table
CREATE TABLE IF NOT EXISTS dictionary (
    id INT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(255) NOT NULL,
    meaning TEXT NOT NULL
);

-- Insert initial data into the dictionary table
INSERT INTO dictionary (word, meaning) VALUES
('apple', 'A crispy round fruit.'),
('apple', 'A brand of electronic devices.'),
('apple', '🍎'),
('banana', 'A yellow fruit.'),
('banana', '🍌'),
('green', 'A colour.'),
('green', 'Environmentally friendly.'),
('green', 'A type of tea.'),
('green', '💚');