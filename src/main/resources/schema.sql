CREATE TABLE IF NOT EXISTS Post (
    id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    author_id INT NOT NULL,
    content VARCHAR(2047) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version INT,
    PRIMARY KEY (id)
);