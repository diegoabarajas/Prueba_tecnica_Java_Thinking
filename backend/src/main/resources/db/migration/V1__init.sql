-- V1: esquema inicial según la prueba técnica

-- Empresas
CREATE TABLE empresas (
  nit            VARCHAR(32) PRIMARY KEY,
  nombre         VARCHAR(255) NOT NULL,
  direccion      VARCHAR(255),
  telefono       VARCHAR(50)
);

-- Productos
CREATE TABLE productos (
  codigo         VARCHAR(64) PRIMARY KEY,
  nombre         VARCHAR(255) NOT NULL,
  caracteristicas TEXT,
  empresa_nit    VARCHAR(32) NOT NULL,
  CONSTRAINT fk_productos_empresa
    FOREIGN KEY (empresa_nit) REFERENCES empresas (nit)
);

-- Categorías
CREATE TABLE categorias (
  id             BIGSERIAL PRIMARY KEY,
  nombre         VARCHAR(255) NOT NULL
);

-- Producto <-> Categoría (M:N)
CREATE TABLE producto_categoria (
  producto_codigo VARCHAR(64) NOT NULL,
  categoria_id    BIGINT NOT NULL,
  PRIMARY KEY (producto_codigo, categoria_id),
  CONSTRAINT fk_pc_producto FOREIGN KEY (producto_codigo) REFERENCES productos (codigo),
  CONSTRAINT fk_pc_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id)
);

-- Clientes
CREATE TABLE clientes (
  id             BIGSERIAL PRIMARY KEY,
  correo         VARCHAR(255) NOT NULL UNIQUE,
  nombre         VARCHAR(255)
);

-- Órdenes
CREATE TABLE ordenes (
  id             BIGSERIAL PRIMARY KEY,
  cliente_id     BIGINT NOT NULL,
  fecha_creacion TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT fk_orden_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

-- Órdenes <-> Productos (M:N) con cantidad
CREATE TABLE orden_producto (
  orden_id        BIGINT NOT NULL,
  producto_codigo VARCHAR(64) NOT NULL,
  cantidad        INTEGER NOT NULL DEFAULT 1,
  PRIMARY KEY (orden_id, producto_codigo),
  CONSTRAINT fk_op_orden FOREIGN KEY (orden_id) REFERENCES ordenes (id),
  CONSTRAINT fk_op_producto FOREIGN KEY (producto_codigo) REFERENCES productos (codigo)
);

-- Usuarios (para autenticación)
CREATE TABLE usuarios (
  id             BIGSERIAL PRIMARY KEY,
  email          VARCHAR(255) NOT NULL UNIQUE,
  password_hash  VARCHAR(255) NOT NULL,
  rol            VARCHAR(32) NOT NULL
);

