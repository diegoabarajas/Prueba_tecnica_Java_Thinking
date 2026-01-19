# PLAN_PROYECTO — Estado y próximos pasos

Este documento resume **qué está hecho**, **qué falta** y el plan de cierre para entregar el proyecto según `ap_multi_vistas.md`.

> Nota: este plan se mantiene simple a propósito. La guía “de verdad” para ejecutar y entender el proyecto está en `README.md`.

## Estado actual (resumen)

### ✅ Implementado

- **Frontend (React + Vite + TS + MUI)**:
  - Login (validación real contra backend)
  - Empresas (CRUD según rol)
  - Productos (crear/listar por empresa, precios multi-moneda)
  - Inventario (listar por empresa, descargar PDF, enviar PDF por correo)
  - Navegación visible solo después de login

- **Backend (Spring Boot + PostgreSQL + Flyway)**:
  - Arquitectura **Hexagonal** (Ports & Adapters)
  - Seguridad: **HTTP Basic** con roles `ADMIN` / `EXTERNO`
  - Endpoints + validaciones + manejo de errores consistente (`ApiErrorResponse`)
  - Observabilidad: `CorrelationIdFilter` (header + MDC logs)
  - PDF de inventario (PDFBox) con soporte UTF-8
  - Email con adjunto PDF usando AWS SES (configurable por variables de entorno)
  - Tablas del punto f) implementadas en backend: categorías, clientes, órdenes (relaciones M:N y 1:N)

- **Tests**:
  - Unit tests (Mockito) y smoke tests de seguridad y observabilidad

### 🟡 Pendiente (para cierre y entrega)

- **Despliegue en AWS** (requisito h):
  - Backend (Elastic Beanstalk o ECS Fargate)
  - Frontend (S3 + CloudFront)
  - Base de datos (RDS PostgreSQL)
  - SES (verificación y/o salida de sandbox)
- **Entregables**:
  - Link de la app en AWS
  - Credenciales finales de Admin y Externo (documentadas)
  - README final completo

## Decisiones de implementación (actuales)

- **DB local**: PostgreSQL 14.5 en Laragon
- **Migraciones**: Flyway (`backend/src/main/resources/db/migration`)
- **Auth**: HTTP Basic (suficiente para la prueba; evita complejidad de JWT)
- **Monedas permitidas (precio producto)**: `COP`, `USD`, `EU` (precio tipo `double`)

## Próximos pasos recomendados (AWS)

1. **RDS PostgreSQL**: crear instancia (dev), configurar `DB_URL/DB_USER/DB_PASSWORD` en backend.
2. **Backend**: desplegar (recomendado primero Elastic Beanstalk por simplicidad).
3. **Frontend**: `npm run build` y subir a S3 (static hosting) + CloudFront opcional.
4. **SES**: verificar remitente/dominio y salir de sandbox si se requiere.
5. Validación final:
   - Login, CRUD empresas/productos, PDF inventario, email inventario.

