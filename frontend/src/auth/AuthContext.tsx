import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import type { AuthState } from "../api/http";
import { authApi } from "../api/auth";
import { clearAuth, loadAuth, saveAuth } from "./authStorage";

/**
 * Contexto de autenticación del frontend.
 *
 * <p>Responsabilidad:
 * - Mantener el estado de sesión (token Basic, email, rol) para toda la SPA.
 * - Exponer helpers: login/logout + flags isAuthed/isAdmin.
 *
 * <p>Decisión de diseño:
 * - El frontend NO decide el rol. El rol se valida contra backend con /api/auth/me.
 * - Esto evita que el usuario pueda “fingir” rol desde la UI.
 */
type AuthContextValue = {
  auth: AuthState;
  isAuthed: boolean;
  isAdmin: boolean;
  loginBasic: (email: string, password: string, role: AuthState["role"]) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [auth, setAuth] = useState<AuthState>(() => loadAuth());

  // Si hay sesión guardada en sessionStorage, la validamos contra backend.
  // Si el backend está caído o las credenciales ya no sirven, limpiamos la sesión.
  useEffect(() => {
    const token = auth.basicToken;
    if (!token) return;

    let cancelled = false;
    (async () => {
      try {
        // Validación server-side de credenciales y rol. Si esto falla, forzamos re-login.
        const me = await authApi.me(token);
        const role = me.role === "ADMIN" ? "ADMIN" : "EXTERNO";
        if (!cancelled) {
          const next: AuthState = { ...auth, email: me.email, role };
          setAuth(next);
          saveAuth(next);
        }
      } catch (e) {
        // 401 o error de red => pedir login de nuevo.
        if (!cancelled) {
          setAuth({});
          clearAuth();
        }
      }
    })();

    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const value = useMemo<AuthContextValue>(() => {
    const isAuthed = Boolean(auth.basicToken);
    const isAdmin = auth.role === "ADMIN";
    return {
      auth,
      isAuthed,
      isAdmin,
      loginBasic(email, password, role) {
        // Importante:
        // - btoa genera Base64(user:pass) para Authorization: Basic ...
        // - En producción se prefiere un token (JWT) en lugar de credenciales codificadas.
        const basicToken = btoa(`${email}:${password}`);
        const next: AuthState = { basicToken, email, role };
        setAuth(next);
        saveAuth(next);
      },
      logout() {
        // Logout local (limpia almacenamiento y estado). En Basic Auth no hay sesión server-side que invalidar.
        setAuth({});
        clearAuth();
      },
    };
  }, [auth]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de AuthProvider");
  return ctx;
}

