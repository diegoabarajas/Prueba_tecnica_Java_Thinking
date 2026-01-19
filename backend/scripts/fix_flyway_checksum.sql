-- Script para reparar el checksum de Flyway después de modificar V5
-- Ejecutar este script directamente en PostgreSQL usando DBeaver o psql

-- Actualizar el checksum de la migración V5 para que coincida con el archivo actual
UPDATE flyway_schema_history 
SET checksum = -521295149 
WHERE version = '5' AND checksum = -1912614292;

-- Verificar que se actualizó correctamente
SELECT version, description, checksum, installed_on 
FROM flyway_schema_history 
WHERE version = '5';
