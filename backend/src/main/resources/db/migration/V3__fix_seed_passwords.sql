-- V3: corregir hashes BCrypt para los usuarios seed
-- Objetivo: que el Basic Auth funcione con el password "ChangeMe123!"
--
-- Usamos pgcrypto para generar BCrypt dentro de PostgreSQL.
-- Nota: En la mayoría de instalaciones PostgreSQL viene disponible.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE usuarios
SET password_hash = crypt('ChangeMe123!', gen_salt('bf', 10))
WHERE email IN ('admin@local.test', 'externo@local.test');

