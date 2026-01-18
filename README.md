# Prueba técnica — Java (Spring Boot) + React + PostgreSQL

Proyecto en construcción siguiendo el enunciado en `ap_multi_vistas.md`.

## Estructura

- `backend/`: API Spring Boot
- `frontend/`: React (Vite)
- `docs/`: documentación (arquitectura, Scrum, etc.)

## Requisitos

- Java 17+ (en local estás usando Java 21/25 y funciona)
- PostgreSQL 14.5 (Laragon)
- Node.js + npm (para frontend)

## Backend (API)

### Configuración de BD

Por defecto el backend usa variables de entorno (con valores por defecto):

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/prueba_tecnica_thinking`)
- `DB_USER` (default: `postgres`)
- `DB_PASSWORD` (default: vacío)

### Ejecutar

```bash
cd backend
./mvnw spring-boot:run
```

### Tests

Para correr la suite de tests:

```bash
cd backend
./mvnw test
```

Notas:
- `PruebaTecnicaApplicationTests` corre con perfil `test` usando **H2 en memoria** (no requiere PostgreSQL local).
- Los unit tests de servicios usan Mockito.

### Endpoints actuales (dev)

- `GET /api/health` → `ok`
- **Empresas**
  - `GET /api/empresas` (público)
  - `POST /api/empresas` (ADMIN via Basic Auth)
- **Productos**
  - `GET /api/productos` (público)
  - `POST /api/productos` (ADMIN via Basic Auth)
- **Inventario**
  - `GET /api/inventario?empresaNit=...` (público)
  - `GET /api/inventario/pdf?empresaNit=...` (público, descarga PDF)
  - `POST /api/inventario/email` (ADMIN via Basic Auth, envía PDF por AWS SES)

### AWS SES (dev local)

Variables requeridas para enviar correo:
- `AWS_REGION` (ej: `us-east-1`)
- `AWS_SES_FROM` (email verificado en SES)
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

### Usuarios (dev)

En la tabla `usuarios` existen:
- `admin@local.test` (rol `ADMIN`)
- `externo@local.test` (rol `EXTERNO`)

Password (dev): `ChangeMe123!`

## Frontend

```bash
cd frontend
npm install
npm run dev
```

