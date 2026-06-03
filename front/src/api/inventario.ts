import { apiFetch, type AuthState } from "./http";

export type InventarioItem = {
  empresaNit: string;
  productoCodigo: string;
  productoNombre: string;
  caracteristicas?: string | null;
};

export type InventarioEmailPayload = {
  empresaNit: string;
  toEmail: string;
  subject?: string;
  message?: string;
};

export const inventarioApi = {
  list: (empresaNit: string) =>
    apiFetch<InventarioItem[]>(`/api/inventario?empresaNit=${encodeURIComponent(empresaNit)}`),
  downloadPdf: (empresaNit: string) =>
    apiFetch<Blob>(`/api/inventario/pdf?empresaNit=${encodeURIComponent(empresaNit)}`, { expectBlob: true }),
  sendEmail: (auth: AuthState, body: InventarioEmailPayload) =>
    apiFetch<void>("/api/inventario/email", { method: "POST", body: JSON.stringify(body), auth }),
};

