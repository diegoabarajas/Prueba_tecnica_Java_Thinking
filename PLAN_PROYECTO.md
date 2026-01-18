# Plan paso a paso (editable) — Prueba técnica Java/Spring Boot + React + PostgreSQL + AWS

Este documento es **el plan que vamos a ir modificando** mientras ejecutamos la prueba.  
Objetivo: construir las vistas/funcionalidades descritas en `ap_multi_vistas.md` con buenas prácticas.

## Alcance (según `ap_multi_vistas.md`)

- **Vistas**:
  - Empresa (CRUD; Admin puede crear/editar/eliminar; Externo solo ver)
  - Productos (registrar por empresa; precio en varias monedas)
  - Login (correo + contraseña; contraseña encriptada)
  - Inventario (tabla productos por empresa; exportar PDF; enviar PDF por correo usando AWS)
- **Usuarios**:
  - Administrador
  - Externo
- **Modelo BD**: Empresa, Productos, Categorías, Clientes, Órdenes
  - Producto ↔ Categorías (muchos a muchos)
  - Cliente → Órdenes (uno a muchos)
  - Órdenes ↔ Productos (muchos a muchos)

## Decisiones (modificables)

- **Backend**: Spring Boot 3.x + Java 17 (o 21). Build: **Maven Wrapper** (no requiere Maven instalado globalmente).
- **Auth**: Spring Security + JWT (simple): usarlo asi con estos dos
- **DB**: PostgreSQL (local con Docker) + Flyway para migraciones.
- **Frontend**: React con Vite + TypeScript.
- **AWS (básico)**:
  - DB: RDS PostgreSQL
  - Backend: Elastic Beanstalk o ECS Fargate (empezar por **Elastic Beanstalk** si quieres menos complejidad)
  - Frontend estático: S3 + CloudFront
  - Email: **SES** (para enviar el PDF)

## Estructura de repositorio (propuesta)

```
/
  backend/        # Spring Boot
  frontend/       # React (Vite)
  docker/         # docker-compose, scripts, docs
  docs/           # diagramas, decisiones, etc.
```

## Checklist de ejecución (lo vamos marcando)

### 0) Preparación local

- [ ] Confirmar versiones: `java -version`, `node -v`, `npm -v`
- [ ] Confirmar PostgreSQL (Laragon): versión **14.5-1** y servicio levantado
- [ ] Crear repo/estructura de carpetas (`backend/`, `frontend/`, `docker/`, `docs/`)
- [ ] Definir puertos locales:
  - Backend: `8080`
  - Frontend: `5173` (Vite)
  - Postgres: `5432` (típico; confirmar en Laragon)

### 1) Base de datos PostgreSQL local (Laragon 6.0)

- [ ] Confirmar datos de conexión en Laragon:
  - Host: `localhost`
  - Puerto: `5432` (o el que tenga Laragon)
  - Usuario: (por confirmar, típico `postgres`)
  - Password: (por confirmar)
- [ ] Crear base de datos: `prueba_tecnica_thinking`
- [ ] (Recomendado) Crear usuario de app: `prueba_app` con password y permisos sobre `prueba_tecnica_thinking`
- [ ] Verificar conexión con un cliente (pgAdmin / DBeaver / psql)

#### Comandos útiles (si tienes `psql` disponible)

```sql
-- Crear DB
CREATE DATABASE prueba_tecnica_thinking;

-- Crear usuario de la app
CREATE USER prueba_app WITH PASSWORD 'cambia_esto';

-- Dar permisos
GRANT ALL PRIVILEGES ON DATABASE prueba_tecnica TO prueba_app;
```

> Nota: en PostgreSQL, los permisos a nivel de tablas/esquemas los terminamos de ajustar después de que Flyway cree las tablas.

### 2) Backend Spring Boot (API)

#### 2.1 Crear proyecto base
- [ ] Generar proyecto en Spring Initializr (Maven, Java 17/21):
  - Dependencies: Spring Web, Spring Data JPA, Validation, Spring Security, PostgreSQL Driver, Flyway
- [ ] Moverlo a `backend/`
- [ ] Probar build: `./mvnw -v` y `./mvnw test`

#### 2.2 Configurar conexión a Postgres
- [ ] `application.yml` con `spring.datasource.*`
- [ ] Configurar JPA (naming/ddl) y Flyway
- [ ] Verificar arranque: `./mvnw spring-boot:run`

**Valores esperados (dev/local con Laragon, ajustables):**
- JDBC: `jdbc:postgresql://localhost:5432/prueba_tecnica_thinking`
- User: `prueba_app`
- Pass: `cambia_esto`

#### 2.3 Modelado y migraciones (Flyway)
- [ ] Definir tablas:
  - `empresas` (nit PK, nombre, direccion, telefono)
  - `productos` (codigo PK, nombre, caracteristicas, empresa_nit FK, ...)
  - `categorias` (id PK, nombre)
  - `producto_categoria` (producto_codigo FK, categoria_id FK) [M:N]
  - `clientes` (id PK, correo, nombre, ...)
  - `ordenes` (id PK, cliente_id FK, fecha, ...)
  - `orden_producto` (orden_id FK, producto_codigo FK, cantidad, precio, ...) [M:N]
  - `usuarios` (id, email, password_hash, rol)
- [ ] Crear migraciones `V1__init.sql`, `V2__...sql`, etc.
- [ ] Implementar entidades JPA y repositorios

#### 2.4 Autenticación y autorización
- [ ] Implementar login:
  - Admin: email + password (BCrypt)
  - Externo: puede ser “rol” sin password (según interpretación) o usuario normal
- [ ] Proteger endpoints:
  - Admin: crear/editar/eliminar empresa; registrar productos; inventario; exportar/enviar PDF
  - Externo: solo lectura de empresas/productos (según definas)
- [ ] Seed de usuarios (Flyway o CommandLineRunner) con credenciales para entregar

#### 2.5 Endpoints REST mínimos
- [ ] Empresas:
  - `GET /api/empresas` (público o externo)
  - `GET /api/empresas/{nit}`
  - `POST /api/empresas` (admin)
  - `PUT /api/empresas/{nit}` (admin)
  - `DELETE /api/empresas/{nit}` (admin)
- [ ] Productos:
  - `GET /api/productos?empresaNit=...`
  - `POST /api/productos` (admin)
- [ ] Inventario:
  - `GET /api/inventario?empresaNit=...`
  - `GET /api/inventario/pdf?empresaNit=...` (descarga PDF)
  - `POST /api/inventario/email` (envía PDF por email via AWS)

#### 2.6 PDF + Email (AWS)
- [ ] Generar PDF en backend (ej: iText / OpenPDF / Apache PDFBox)
- [ ] Integración AWS SES:
  - Variables: `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`
  - Verificación de email en SES (sandbox vs producción)
- [ ] Endpoint para enviar PDF como adjunto (o link a S3)

### 3) Frontend React (Vite)

#### 3.1 Crear proyecto base
- [ ] `npm create vite@latest frontend -- --template react-ts`
- [ ] Instalar dependencias: router, axios, UI (opcional)
- [ ] Configurar `.env` (API base URL)

#### 3.2 Pantallas (según vistas)
- [ ] Login
- [ ] Empresas (lista + formulario admin)
- [ ] Productos (formulario + listado por empresa)
- [ ] Inventario (tabla + botón “Descargar PDF” + botón “Enviar por email”)

#### 3.3 Roles/guardas
- [ ] Guardar token (si JWT) y proteger rutas admin
- [ ] Manejo de errores y estados (loading, etc.)

### 4) Despliegue AWS (simple y guiado)

> La idea es que sea entendible: primero desplegamos “lo mínimo”, luego mejoramos.

- [ ] Crear cuenta/usuario IAM (si aplica) y credenciales
- [ ] RDS PostgreSQL:
  - Crear instancia (dev) y permitir acceso desde backend
- [ ] Backend:
  - Opción A (más simple): Elastic Beanstalk (Java)
  - Opción B: ECS Fargate (Docker)
  en esta parte primero la opcion a y luego la opcion b cuando ya este todo bien, solo sea ajustar ala opcion B
- [ ] Frontend:
  - Build: `npm run build`
  - S3 (static hosting) + CloudFront (opcional)
- [ ] SES:
  - Verificar emails
  - Configurar permisos
- [ ] Variables de entorno en AWS (DB, JWT secret, AWS region, etc.)

### 5) Entregables / documentación

- [ ] `README.md` (cómo correr local, variables, comandos)
- [ ] Credenciales de usuarios (Admin y Externo) para entregar
- [ ] Link a despliegue AWS
- [ ] Diagramas (ERD + arquitectura) en `docs/`

## Próximo paso (elige 1)

1) Confirmamos los datos de conexión de Laragon (**host/puerto/usuario/password**) y creamos la DB/usuario. -> esto esta configurado: usuario estandard o ya deberias tenerlo
ademas tengo dbeaver, dime paso apaso para gestionar la base de datos, cuando se requiera
2) Creamos el esqueleto del repo (`backend/` + `frontend/`) y dejamos el backend conectando a PostgreSQL.

Dime estos 3 datos de tu Laragon para dejarlo 100% exacto en el plan:
- **Puerto** de PostgreSQL = 5432
- **Usuario** y **password** que estás usando (o si creamos `prueba_app`) -> si creemos esto y ayudame a generarlo paso a paso
- Si Laragon te expone `psql` en PATH (no)

