# Scrum (guía rápida para esta prueba)

## Objetivo del sprint (MVP)

Entregar una app desplegada en AWS que cumpla:

- CRUD Empresas (con roles)
- Registro/Listado Productos por Empresa
- Inventario por Empresa + descarga PDF
- Envío del PDF por correo usando un servicio AWS (SES)
- README + credenciales + enlaces de despliegue

## Backlog (alto nivel)

### Épica: Autenticación y roles

- Login (Admin/Externo)
- Autorización por endpoints (Admin vs público)

### Épica: Empresas

- CRUD + validaciones
- UI (React)

### Épica: Productos / Inventario

- Productos por empresa (API + UI)
- Inventario (API + UI)
- PDF de inventario

### Épica: AWS

- Backend desplegado
- Frontend desplegado
- BD (RDS)
- Email (SES)

## Definición de Hecho (DoD)

- Funciona en local desde README
- Datos persistidos en PostgreSQL
- Endpoints protegidos según rol
- Sin errores de compilación
- Documentación mínima (README + decisiones)

## Estado actual (resumen)

- **Hecho**: Empresas (UI + API), Productos (UI + API + precios multi-moneda), Inventario (UI + API + PDF + Email), Seguridad (Basic + roles), tablas del punto f) implementadas en backend (categorías / clientes / órdenes), logging con correlationId y errores consistentes.
- **Pendiente**: despliegue en AWS (backend + frontend + RDS + SES) y consolidación final de entregables (README final + enlaces).

