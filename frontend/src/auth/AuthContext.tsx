import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import type { AuthState } from "../api/http";
import { authApi } from "../api/auth";
import { clearAuth, loadAuth, saveAuth } from "./authStorage";

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
        const basicToken = btoa(`${email}:${password}`);
        const next: AuthState = { basicToken, email, role };
        setAuth(next);
        saveAuth(next);
      },
      logout() {
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

