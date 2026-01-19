import type { AuthState } from "../api/http";

const KEY = "auth_v1";

/**
 * Persistencia de la sesión en el navegador.
 *
 * <p>Decisión de diseño:
 * - Usamos sessionStorage (no localStorage) para que al cerrar el navegador, la sesión se borre.
 * - En una app productiva con tokens (JWT), normalmente se usaría un flujo más robusto.
 */
export function loadAuth(): AuthState {
  try {
    const raw = sessionStorage.getItem(KEY);
    if (!raw) return {};
    const parsed = JSON.parse(raw) as AuthState;
    if (!parsed || typeof parsed !== "object") return {};
    return parsed;
  } catch {
    return {};
  }
}

/**
 * Guarda la sesión actual (token Basic + email + rol).
 *
 * <p>Nota: el Basic token es equivalente a guardar usuario/contraseña codificados en Base64.
 * Para una prueba técnica se aceptó por simplicidad, pero en producción se prefiere JWT/OAuth2.
 */
export function saveAuth(auth: AuthState) {
  sessionStorage.setItem(KEY, JSON.stringify(auth));
}

/**
 * Elimina la sesión actual del navegador.
 */
export function clearAuth() {
  sessionStorage.removeItem(KEY);
}

