-- V5: precio como double precision y monedas limitadas a COP/USD/UE (luego cambiado a EU en V6)

-- Cambiar tipo de precio a double precision (si ya existe data, hacemos cast)
ALTER TABLE producto_precios
  ALTER COLUMN precio TYPE double precision
  USING (precio::double precision);

-- Normalizar moneda a mayúsculas para cumplir constraint
UPDATE producto_precios SET moneda = UPPER(moneda);

-- Limitar monedas (solo COP, USD, UE) - Nota: cambiado a EU en V6
ALTER TABLE producto_precios
  ADD CONSTRAINT chk_producto_precios_moneda
  CHECK (moneda IN ('COP', 'USD', 'UE'));

