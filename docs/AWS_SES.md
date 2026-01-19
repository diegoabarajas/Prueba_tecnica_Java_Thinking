# AWS SES — Enviar el PDF por correo (paso a paso)

Esta guía explica lo mínimo para cumplir el requisito: **enviar por correo el PDF del inventario usando AWS**.

## 0) Requisitos previos

- Tener una **cuenta AWS** (si no tienes: crearla).
- Tener un correo que usarás como **remitente** (FROM) y un correo **destinatario** (TO).

> En modo **sandbox** de SES normalmente debes **verificar** tanto el remitente como el destinatario.

## 1) Crear cuenta AWS (si no tienes)

1. Crear cuenta en AWS (necesita método de pago).
2. Activar MFA en el usuario root.
3. Crear un usuario IAM para desarrollo (no uses root).

## 2) Crear credenciales IAM (dev)

En IAM:
1. Crea un **usuario** (por ejemplo `prueba-tecnica-dev`).
2. Asigna permisos mínimos (política administrada o custom):
   - Para SES: permiso para enviar (`ses:SendRawEmail`).
3. Crea **Access key** (Access key ID / Secret access key).

En local, define variables de entorno:

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION` (ej: `us-east-1`)

## 3) Configurar SES

1. En AWS Console: Amazon SES (en la región elegida).
2. **Verificar identidad**:
   - Rápido: verificar un **email** (FROM).
   - Recomendado: verificar un **dominio** (requiere DNS para DKIM/SPF).
3. Si estás en sandbox:
   - Verifica también el **email destinatario** (TO), o
   - Solicita “**Production access**” para enviar a cualquier correo.

## 3.1) Evitar que los correos lleguen a SPAM (recomendado)

Si usas un email personal (Gmail/Outlook) como FROM, es común que caiga en spam.
Para producción o demo “seria”, lo ideal es:

- Verificar un **dominio** en SES (no solo un email).
- Configurar **SPF** y **DKIM** en tu DNS (Route53 u otro).
- Opcional: configurar **DMARC**.

Con esto mejoras mucho la reputación/entregabilidad.

## 4) Configurar el backend

Variables de entorno necesarias:

- `AWS_REGION` (ej: `us-east-1`)
- `AWS_SES_FROM` (ej: `admin@tudominio.com` o email verificado en SES)
- `AWS_ACCESS_KEY_ID` y `AWS_SECRET_ACCESS_KEY` (solo para dev local)

### Ejemplo (Windows PowerShell)

```powershell
$env:AWS_REGION="us-east-1"
$env:AWS_SES_FROM="tu_email_verificado@ejemplo.com"
$env:AWS_ACCESS_KEY_ID="AKIA..."
$env:AWS_SECRET_ACCESS_KEY="..."
```

## 5) Probar el envío (API)

Endpoint (ADMIN):

- `POST /api/inventario/email`

Body JSON:

```json
{
  "empresaNit": "900123456",
  "toEmail": "destino@correo.com",
  "subject": "Inventario",
  "message": "Adjunto el inventario en PDF."
}
```

Respuesta esperada:
- 200 OK (o 202 Accepted) si SES aceptó el envío.

## Errores comunes

- **`SignatureDoesNotMatch (403)`**: Access key/secret incorrectos o incompletos; revisar que las variables estén en la misma consola donde se ejecuta el backend.
- **Sandbox**: si el destinatario no está verificado, SES rechaza el envío.
