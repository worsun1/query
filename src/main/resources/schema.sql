CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO customers (name, email) VALUES
    ('Alice Wang', 'alice@example.com'),
    ('Bob Li', 'bob@example.com'),
    ('Charlie Zhang', 'charlie@example.com')
ON DUPLICATE KEY UPDATE name = VALUES(name);
