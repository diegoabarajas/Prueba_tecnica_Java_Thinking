# Arquitectura (estado actual)

## Objetivo

Mantener un backend entendible y extensible, con responsabilidades claras por capa.

## Backend (Spring Boot)

Paquetes principales:

- `config/`: configuración (seguridad, etc.)
- `common/`: manejo de errores y utilidades transversales
- `empresa/`, `producto/`, `inventario/`: módulos por dominio

Estructura por capas (por módulo):

- **Controller**: HTTP (request/response). Validación de entrada con `@Valid`.
- **Service**: reglas de negocio, transacciones (`@Transactional`), orquestación.
- **Repository**: acceso a datos (JPA).
- **DTOs**: requests/responses para no exponer entidades JPA directamente.

## Seguridad (dev)

Actualmente: **HTTP Basic** + roles `ADMIN` / `EXTERNO`.
Esto permite validar rápidamente permisos para el CRUD.

Más adelante, si el frontend requiere una experiencia de login mejor, se migra a **JWT** (sin cambiar el dominio).

## Base de datos

- PostgreSQL
- Migraciones: Flyway (`backend/src/main/resources/db/migration`)

