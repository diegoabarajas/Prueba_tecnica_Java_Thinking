# Arquitectura (estado actual)

## Objetivo

Mantener el backend con **responsabilidades claras** y con un diseño que permita:

- Evolucionar el dominio sin acoplarlo a frameworks (Spring, JPA, AWS SDK, PDFBox).
- Sustituir infraestructura (DB/Email/PDF) sin tocar reglas de negocio.
- Probar casos de uso con unit tests (mock de puertos) y smoke tests de endpoints.

## Estilo: Hexagonal (Ports & Adapters)

El backend sigue un enfoque **Hexagonal** (Ports & Adapters):

- **Core**: modelos + casos de uso + puertos (interfaces).
- **Adapters**:
  - **Inbound**: REST (controladores/DTOs).
  - **Outbound**: persistencia (JPA) y servicios externos (PDF, Email).

Estructura (alto nivel):

```
backend/src/main/java/com/diegoabarajas/pruebatecnica
  core/
    application/        # casos de uso + modelos del core
    ports/out/          # interfaces (puertos) hacia infraestructura
  adapters/
    in/rest/            # controllers + DTOs + seguridad + manejo de error
    out/persistence/    # entidades JPA + repos + adapters que implementan puertos
    out/pdf/            # adapter PDF (PDFBox)
    out/email/          # adapter Email (AWS SES)
```

## Capas y responsabilidades

### Core

- **Modelos del core** (records/classes): representan el “lenguaje” del negocio (Company, Product, Order, etc.).
- **Casos de uso** (`core/application/*Service`): orquestan validaciones y llamadas a puertos.
- **Puertos** (`core/ports/out/*`): contratos que el core necesita (persistir, generar PDF, enviar email).

> Regla: el core **no importa** Spring MVC, JPA, ni AWS SDK.

### Adapters inbound (REST)

Ubicación: `adapters/in/rest/*`

- **Controllers**: exponen endpoints, validan entrada con Bean Validation y mapean a comandos del core.
- **DTOs**: requests/responses estables (evitan exponer entidades JPA).
- **Seguridad**: configuración de Spring Security y CORS.
- **Errores**: respuesta consistente (`ApiExceptionHandler` / `ApiErrorResponse`).
- **Observabilidad**: `CorrelationIdFilter` para trazabilidad (header y logs).

### Adapters outbound (infraestructura)

Ubicación: `adapters/out/*`

- **Persistencia (JPA)**: entidades y repositorios Spring Data.
  - Los *RepositoryAdapter* traducen entre entidades JPA y modelos core e implementan puertos.
- **PDF**: `InventarioPdfRenderer` implementa el puerto `InventarioPdfRendererPort`.
- **Email**: `SesEmailService` implementa `EmailSenderPort`.

## Seguridad (dev/local)

- Autenticación: **HTTP Basic**.
- Roles: `ADMIN` y `EXTERNO`.
- Convención Spring: se expone como `ROLE_ADMIN` / `ROLE_EXTERNO`.
- Lectura pública en endpoints de consulta; operaciones de escritura restringidas a `ADMIN`.

## Base de datos y migraciones

- Motor: PostgreSQL (local con Laragon).
- Versionado: Flyway en `backend/src/main/resources/db/migration`.

## Observabilidad y errores

- **Correlation ID**:
  - Se acepta `X-Correlation-Id`/`X-Request-Id` y si no existe se genera.
  - Se retorna `X-Correlation-Id` en la respuesta y se registra en logs via MDC.
- **Errores consistentes**:
  - JSON con `errorCode`, `message`, `fieldErrors` (cuando aplica) + `correlationId`.

## Pruebas

- **Unit tests (Mockito)**: prueban servicios del core mockeando puertos.
- **WebMvc tests**: verifican reglas de seguridad, correlation id y formatos de respuesta.
- **SpringBootTest** con perfil `test` usando **H2** (evita depender de PostgreSQL para correr tests).
