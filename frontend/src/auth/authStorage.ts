import type { AuthState } from "../api/http";

const KEY = "auth_v1";

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

export function saveAuth(auth: AuthState) {
  sessionStorage.setItem(KEY, JSON.stringify(auth));
}

export function clearAuth() {
  sessionStorage.removeItem(KEY);
}

