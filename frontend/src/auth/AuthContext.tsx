import React, { createContext, useContext, useMemo, useState } from "react";
import type { AuthState } from "../api/http";
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

