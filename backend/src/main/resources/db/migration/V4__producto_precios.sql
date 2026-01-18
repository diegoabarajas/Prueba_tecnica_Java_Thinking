-- V4: precios por producto en múltiples monedas

CREATE TABLE producto_precios (
  id              BIGSERIAL PRIMARY KEY,
  producto_codigo VARCHAR(64) NOT NULL,
  moneda          VARCHAR(3)  NOT NULL,
  precio          NUMERIC(18,2) NOT NULL,
  CONSTRAINT fk_producto_precios_producto
    FOREIGN KEY (producto_codigo) REFERENCES productos (codigo) ON DELETE CASCADE,
  CONSTRAINT uq_producto_precios_moneda
    UNIQUE (producto_codigo, moneda)
);

CREATE INDEX idx_producto_precios_producto ON producto_precios (producto_codigo);

