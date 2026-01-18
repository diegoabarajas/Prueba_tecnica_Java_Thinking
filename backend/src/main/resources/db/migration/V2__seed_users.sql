-- V2: usuarios iniciales (Admin y Externo)
-- Nota: hashes BCrypt pre-generados (puedes cambiar luego).

INSERT INTO usuarios (email, password_hash, rol)
VALUES
  ('admin@local.test',  '$2a$10$zA2D5j8oIYQJ8y8lJm1k1e8mQWmD7mQh3g1y7n6cCw1o7aJvVwH6K', 'ADMIN'),
  ('externo@local.test','$2a$10$zA2D5j8oIYQJ8y8lJm1k1e8mQWmD7mQh3g1y7n6cCw1o7aJvVwH6K', 'EXTERNO')
ON CONFLICT (email) DO NOTHING;

-- Password temporal para ambos: "ChangeMe123!" (cambiar antes de entregar si quieres)

