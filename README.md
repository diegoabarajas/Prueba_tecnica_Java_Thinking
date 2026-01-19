# Prueba técnica — Java (Spring Boot) + React + PostgreSQL

Proyecto full‑stack construido según el enunciado en `ap_multi_vistas.md`.

## Stack

- **Backend**: Java + Spring Boot + Spring Security (HTTP Basic) + JPA + Flyway
- **DB**: PostgreSQL 14.5 (local en Laragon) + migraciones con Flyway
- **Frontend**: React + Vite + TypeScript + MUI
- **PDF**: Apache PDFBox
- **Email**: AWS SES (adjunto PDF)

## Requerimientos del enunciado (mapa rápido)

- **a) Empresa**: ✅ UI + API (CRUD; Admin crea/edita/elimina; Externo lectura)
- **b) Productos**: ✅ UI + API (por empresa; precio en varias monedas)
- **c) Login**: ✅ UI + validación real (correo/contraseña contra backend)
- **d) Inventario**: ✅ UI + API + PDF + envío por correo (SES)
- **e) Roles**: ✅ Admin / Externo
- **f) Modelo**: ✅ implementado en backend (Empresa, Productos, Categorías, Clientes, Órdenes + relaciones)
- **g) Password encriptada**: ✅ BCrypt
- **h) AWS**: 🟡 pendiente (ver sección “Despliegue en AWS”)

## Arquitectura

El backend usa **Arquitectura Hexagonal (Ports & Adapters)**. Detalle en `docs/ARQUITECTURA.md`.

También se implementó:

- **Correlation ID** por request (header `X-Correlation-Id`) y logs con MDC.
- **Errores consistentes** en JSON (incluye `errorCode` y `correlationId`).

## Estructura del repo

- `backend/`: API Spring Boot
- `frontend/`: React (Vite)
- `docs/`: documentación complementaria

## Requisitos para correr local

- **Java 17+** (probado con Java 21)
- **Node.js + npm** (para el frontend)
- **PostgreSQL 14.5** (Laragon)

## Backend (API)

### 1) Configuración de base de datos

El backend usa variables de entorno (con defaults):

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/prueba_tecnica_thinking`)
- `DB_USER` (default: `postgres`)
- `DB_PASSWORD` (default: vacío)

> En Laragon se suele usar `postgres` sin password (como en este proyecto).

### 2) Ejecutar backend

En Windows (CMD/PowerShell):

```bash
cd backend
.\mvnw.cmd -DskipTests spring-boot:run
```

Health check:

- `GET http://localhost:8080/api/health` → `ok`

### 3) Migraciones (Flyway)

Las migraciones están en `backend/src/main/resources/db/migration`.

Comandos útiles:

```bash
cd backend
.\mvnw.cmd flyway:info
.\mvnw.cmd flyway:migrate
```

Si aparece un error de checksum (por ejemplo después de tocar una migración ya aplicada), reparar:

```bash
cd backend
.\mvnw.cmd -Dflyway.url=jdbc:postgresql://localhost:5432/prueba_tecnica_thinking -Dflyway.user=postgres -Dflyway.password= flyway:repair
```

### 4) Usuarios de prueba (seed)

Credenciales (dev):

- **ADMIN**: `admin@local.test` / `ChangeMe123!`
- **EXTERNO**: `externo@local.test` / `ChangeMe123!`

### 5) Seguridad (HTTP Basic)

- Lecturas: en general **públicas**
- Escrituras (POST/PUT/DELETE): requieren **ADMIN**

El frontend valida credenciales con:

- `GET /api/auth/me` (requiere auth; retorna rol del usuario)

### 6) Endpoints principales

#### Público (lectura)

- `GET /api/empresas`
- `GET /api/productos` (opcional: `?empresaNit=...`)
- `GET /api/inventario?empresaNit=...`
- `GET /api/inventario/pdf?empresaNit=...`
- `GET /api/categorias/**`
- `GET /api/clientes/**`
- `GET /api/ordenes/**`

#### ADMIN (escritura)

- Empresas: `POST/PUT/DELETE /api/empresas/**`
- Productos: `POST /api/productos`
- Inventario email: `POST /api/inventario/email`
- Categorías: `POST/PUT/DELETE /api/categorias/**`
- Asignación categorías a producto: `PUT /api/productos/{codigo}/categorias`
- Clientes: `POST/PUT/DELETE /api/clientes/**`
- Órdenes: `POST /api/ordenes`

> Uso de tablas del punto f): ver `docs/USO_TABLAS_PUNTO_F.md`.

### 7) Envío de correo (AWS SES)

Ver guía paso a paso en `docs/AWS_SES.md`.

Variables requeridas:

- `AWS_REGION` (ej: `us-east-1`)
- `AWS_SES_FROM` (email verificado en SES)
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

## Frontend (React)

### 1) Configurar base URL del backend

Por default usa `http://localhost:8080`. Si quieres cambiarlo, crea `frontend/.env`:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

### 2) Ejecutar frontend

```bash
cd frontend
npm install
npm run dev
```

Abrir:

- `http://localhost:5173`

Notas:

- Las pestañas de navegación se muestran **solo después de login**.
- En Productos e Inventario, el filtro *Empresa NIT* se maneja con **listado desplegable** (carga desde `/api/empresas`).

## Pruebas / Calidad

Backend:

```bash
cd backend
.\mvnw.cmd test
```

Frontend:

```bash
cd frontend
npm run build
```

## Troubleshooting (errores comunes)

- **Puerto 8080 ocupado**
  - Buscar PID: `netstat -ano | findstr :8080`
  - Matar proceso: `taskkill /PID <PID> /F`

- **406 al descargar PDF**
  - El frontend usa `Accept: */*` en descargas para evitar `406 Not Acceptable`.

- **Caracteres especiales en PDF**
  - Se usa fuente TrueType con PDFBox para soportar UTF‑8.

- **SES / correos en spam**
  - Recomendado verificar dominio y configurar DKIM/SPF (ver `docs/AWS_SES.md`).

## Despliegue en AWS (pendiente)

Plan recomendado:

1. **RDS PostgreSQL**
2. **Backend** (Elastic Beanstalk por simplicidad, luego ECS Fargate si se quiere)
3. **Frontend** (S3 static hosting + CloudFront opcional)
4. **SES** (verificación y salida de sandbox si aplica)

Seguimiento: `PLAN_PROYECTO.md`.

## Documentación adicional

- `docs/ARQUITECTURA.md`: arquitectura (hexagonal)
- `docs/USO_TABLAS_PUNTO_F.md`: uso de categorías/clientes/órdenes (punto f)
- `docs/AWS_SES.md`: configuración SES paso a paso
- `docs/UX_UI.md`: decisiones UX/UI
- `docs/SCRUM.md`: guía rápida y estado

