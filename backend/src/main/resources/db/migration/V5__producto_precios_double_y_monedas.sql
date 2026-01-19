-- V5: precio como double precision y monedas limitadas a COP/USD/UE

ALTER TABLE producto_precios
  ALTER COLUMN precio TYPE double precision
  USING (precio::double precision);

UPDATE producto_precios SET moneda = UPPER(moneda);

ALTER TABLE producto_precios
  ADD CONSTRAINT chk_producto_precios_moneda
  CHECK (moneda IN ('COP', 'USD', 'UE'));

