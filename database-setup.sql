-- Create users table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(10) NOT NULL DEFAULT 'USER'
);

-- Insert sample data
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('user', 'user123', 'USER');
