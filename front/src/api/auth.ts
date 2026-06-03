import { apiFetch } from "./http";

export type AuthMe = { email: string; role: "ADMIN" | "EXTERNO" | string };

export const authApi = {
  me: (basicToken: string) =>
    apiFetch<AuthMe>("/api/auth/me", {
      auth: { basicToken },
    }),
};

