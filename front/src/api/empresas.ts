import { apiFetch, type AuthState } from "./http";

export type Empresa = {
  nit: string;
  nombre: string;
  direccion?: string | null;
  telefono?: string | null;
};

export type EmpresaCreate = {
  nit: string;
  nombre: string;
  direccion?: string;
  telefono?: string;
};

export const empresasApi = {
  list: () => apiFetch<Empresa[]>("/api/empresas"),
  create: (auth: AuthState, body: EmpresaCreate) =>
    apiFetch<Empresa>("/api/empresas", { method: "POST", body: JSON.stringify(body), auth }),
  update: (auth: AuthState, nit: string, body: EmpresaCreate) =>
    apiFetch<Empresa>(`/api/empresas/${encodeURIComponent(nit)}`, { method: "PUT", body: JSON.stringify(body), auth }),
  remove: (auth: AuthState, nit: string) =>
    apiFetch<void>(`/api/empresas/${encodeURIComponent(nit)}`, { method: "DELETE", auth }),
};

