import { env } from "../config/env";

/**
 * Tipado del error estándar devuelto por el backend.
 *
 * <p>El backend usa {@code ApiExceptionHandler} para devolver siempre un JSON con esta forma cuando hay errores
 * (validación, 404, 409, 500, etc.). Esto permite mostrar mensajes consistentes en UI.
 */
export type ApiErrorResponse = {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  fieldErrors?: Record<string, string> | null;
};

/**
 * Error que encapsula estado HTTP + body del backend.
 *
 * <p>Se usa para diferenciar:
 * - Errores HTTP (401/403/404/409/500) vs. errores de código del frontend.
 */
export class ApiError extends Error {
  status?: number;
  body?: ApiErrorResponse | unknown;
  constructor(message: string, status?: number, body?: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.body = body;
  }
}

export type AuthState = {
  basicToken?: string; // Base64(user:pass)
  email?: string;
  role?: "ADMIN" | "EXTERNO";
};

/**
 * Cliente HTTP minimalista para toda la app.
 *
 * <p>Responsabilidad:
 * - Construir URL base usando {@code env.apiBaseUrl}
 * - Adjuntar headers comunes (Accept/Content-Type) y Authorization Basic cuando aplica
 * - Parsear respuestas JSON vs texto vs blob (PDF)
 * - Lanzar {@link ApiError} en respuestas no-2xx para que la UI pueda manejar errores uniformemente
 */
export async function apiFetch<T>(
  path: string,
  opts: RequestInit & { auth?: AuthState; expectBlob?: boolean } = {}
): Promise<T> {
  const url = `${env.apiBaseUrl}${path}`;
  const headers = new Headers(opts.headers ?? {});
  // Si descargamos binarios (ej: PDF), no forzamos application/json porque Spring puede responder 406.
  const defaultAccept = opts.expectBlob ? "*/*" : "application/json";
  headers.set("Accept", headers.get("Accept") ?? defaultAccept);

  if (opts.body && !headers.get("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (opts.auth?.basicToken) {
    headers.set("Authorization", `Basic ${opts.auth.basicToken}`);
  }

  const res = await fetch(url, { ...opts, headers });

  if (opts.expectBlob) {
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new ApiError(text || `HTTP ${res.status}`, res.status);
    }
    return (await res.blob()) as unknown as T;
  }

  const contentType = res.headers.get("content-type") ?? "";
  const isJson = contentType.includes("application/json");
  const data = isJson ? await res.json().catch(() => undefined) : await res.text().catch(() => undefined);

  if (!res.ok) {
    const body = data as ApiErrorResponse | unknown;
    const msg =
      (typeof body === "object" && body && "message" in body && (body as any).message) ||
      (typeof data === "string" ? data : undefined) ||
      `HTTP ${res.status}`;
    throw new ApiError(String(msg), res.status, body);
  }

  return data as T;
}

