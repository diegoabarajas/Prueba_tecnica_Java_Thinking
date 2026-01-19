-- V6: Cambiar moneda UE a EU según requerimientos

-- IMPORTANTE:
-- Primero eliminamos el CHECK constraint para permitir actualizar data existente.
ALTER TABLE producto_precios DROP CONSTRAINT IF EXISTS chk_producto_precios_moneda;

-- Normalizar a mayúsculas y reemplazar UE -> EU
UPDATE producto_precios SET moneda = UPPER(moneda);
UPDATE producto_precios SET moneda = 'EU' WHERE moneda = 'UE';

-- Recrear constraint con EU en lugar de UE
ALTER TABLE producto_precios
  ADD CONSTRAINT chk_producto_precios_moneda
  CHECK (moneda IN ('COP', 'USD', 'EU'));
