-- V6: Cambiar moneda UE a EU según requerimientos

-- Actualizar datos existentes
UPDATE producto_precios SET moneda = 'EU' WHERE moneda = 'UE';

-- Eliminar constraint anterior
ALTER TABLE producto_precios DROP CONSTRAINT IF EXISTS chk_producto_precios_moneda;

-- Recrear constraint con EU en lugar de UE
ALTER TABLE producto_precios
  ADD CONSTRAINT chk_producto_precios_moneda
  CHECK (moneda IN ('COP', 'USD', 'EU'));
