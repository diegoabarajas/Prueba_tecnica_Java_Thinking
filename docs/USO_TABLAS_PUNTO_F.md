# Uso de las Tablas según Punto F) de ap_multi_vistas.md

## Requisitos del Punto F)

El punto f) especifica que la base de datos debe cumplir con:
1. **Un Producto puede pertenecer a múltiples Categorías** (relación M:N)
2. **Un Cliente puede tener múltiples Órdenes** (relación 1:N)
3. **Las órdenes pueden tener múltiples Productos** (relación M:N con cantidad)

## Estado de Implementación

### ✅ 1. Producto ↔ Categorías (M:N)

**Tabla de relación:** `producto_categoria`

**Endpoints disponibles:**
- `PUT /api/productos/{codigo}/categorias` (ADMIN) - Asignar categorías a un producto
  ```json
  {
    "categoriaIds": [1, 2, 3]
  }
  ```
- `GET /api/productos/{codigo}/categorias` (Público) - Ver categorías de un producto
  - Retorna: `[1, 2, 3]` (lista de IDs de categorías)
- `GET /api/categorias/{id}/productos` (Público) - Ver productos de una categoría
  - Retorna: Lista de productos con sus precios

**Cómo usar:**
1. Crear categorías: `POST /api/categorias` (ADMIN)
2. Crear productos: `POST /api/productos` (ADMIN)
3. Asignar categorías a un producto: `PUT /api/productos/P001/categorias` con `{"categoriaIds": [1, 2]}`
4. Consultar: `GET /api/productos/P001/categorias` para ver qué categorías tiene un producto
5. Consultar: `GET /api/categorias/1/productos` para ver todos los productos de una categoría

### ✅ 2. Cliente ↔ Órdenes (1:N)

**Tabla:** `ordenes` con `cliente_id`

**Endpoints disponibles:**
- `GET /api/clientes` (Público) - Listar todos los clientes
- `POST /api/clientes` (ADMIN) - Crear cliente
- `GET /api/ordenes?clienteId=1` (Público) - Listar órdenes de un cliente específico
- `GET /api/ordenes` (Público) - Listar todas las órdenes
- `POST /api/ordenes` (ADMIN) - Crear orden para un cliente

**Cómo usar:**
1. Crear cliente: `POST /api/clientes` con `{"correo": "cliente@example.com", "nombre": "Juan Pérez"}`
2. Crear orden para ese cliente: `POST /api/ordenes` con:
   ```json
   {
     "clienteId": 1,
     "items": [
       {"productoCodigo": "P001", "cantidad": 2},
       {"productoCodigo": "P002", "cantidad": 1}
     ]
   }
   ```
3. Consultar órdenes de un cliente: `GET /api/ordenes?clienteId=1`

### ✅ 3. Orden ↔ Productos (M:N con cantidad)

**Tabla de relación:** `orden_producto` con campos:
- `orden_id` (FK a `ordenes`)
- `producto_codigo` (FK a `productos`)
- `cantidad` (INTEGER)

**Endpoints disponibles:**
- `POST /api/ordenes` (ADMIN) - Crear orden con múltiples productos
  ```json
  {
    "clienteId": 1,
    "items": [
      {"productoCodigo": "P001", "cantidad": 5},
      {"productoCodigo": "P002", "cantidad": 3},
      {"productoCodigo": "P003", "cantidad": 1}
    ]
  }
  ```
- `GET /api/ordenes/{id}` (Público) - Ver orden completa con sus productos
  - Retorna:
    ```json
    {
      "id": 1,
      "clienteId": 1,
      "fechaCreacion": "2026-01-19T10:00:00Z",
      "items": [
        {"productoCodigo": "P001", "cantidad": 5},
        {"productoCodigo": "P002", "cantidad": 3}
      ]
    }
    ```

**Cómo usar:**
1. Crear orden con múltiples productos y cantidades: `POST /api/ordenes`
2. Ver detalles de una orden: `GET /api/ordenes/1` (incluye todos los productos con sus cantidades)

## Resumen de Tablas y su Uso

| Tabla | Función | Endpoints |
|-------|---------|-----------|
| `categorias` | Categorías de productos | `/api/categorias` |
| `producto_categoria` | Relación M:N Producto ↔ Categoría | `/api/productos/{codigo}/categorias` |
| `clientes` | Clientes del sistema | `/api/clientes` |
| `ordenes` | Órdenes de compra | `/api/ordenes` |
| `orden_producto` | Relación M:N Orden ↔ Producto (con cantidad) | Incluido en `POST /api/ordenes` |

## Ejemplo Completo de Flujo

1. **Crear categorías:**
   ```bash
   POST /api/categorias {"nombre": "Electrónica"}
   POST /api/categorias {"nombre": "Hogar"}
   ```

2. **Crear productos:**
   ```bash
   POST /api/productos {
     "codigo": "P001",
     "nombre": "Laptop",
     "caracteristicas": "16GB RAM",
     "empresaNit": "900123456",
     "precios": [{"moneda": "COP", "precio": 2000000}]
   }
   ```

3. **Asignar categorías al producto:**
   ```bash
   PUT /api/productos/P001/categorias {"categoriaIds": [1, 2]}
   ```

4. **Crear cliente:**
   ```bash
   POST /api/clientes {"correo": "cliente@test.com", "nombre": "Juan"}
   ```

5. **Crear orden con múltiples productos:**
   ```bash
   POST /api/ordenes {
     "clienteId": 1,
     "items": [
       {"productoCodigo": "P001", "cantidad": 2},
       {"productoCodigo": "P002", "cantidad": 1}
     ]
   }
   ```

6. **Consultar órdenes del cliente:**
   ```bash
   GET /api/ordenes?clienteId=1
   ```

## Nota

Todas estas funcionalidades están **completamente implementadas** y funcionando. Los endpoints están documentados en el código y siguen la arquitectura hexagonal del proyecto.
