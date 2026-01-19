-- Script para reparar el checksum de Flyway después de modificar V5
-- Ejecutar este script directamente en PostgreSQL usando DBeaver o psql

-- Actualizar el checksum de la migración V5 para que coincida con el archivo actual
-- El checksum local calculado por Flyway es: -385259012
UPDATE flyway_schema_history 
SET checksum = -385259012 
WHERE version = '5';

-- Verificar que se actualizó correctamente
SELECT version, description, checksum, installed_on 
FROM flyway_schema_history 
WHERE version = '5';
