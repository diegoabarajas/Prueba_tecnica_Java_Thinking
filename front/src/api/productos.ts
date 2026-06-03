import { apiFetch, type AuthState } from "./http";

export type Producto = {
  codigo: string;
  nombre: string;
  caracteristicas?: string | null;
  empresaNit: string;
  precios?: { moneda: string; precio: number }[];
};

export type ProductoCreate = {
  codigo: string;
  nombre: string;
  caracteristicas?: string;
  empresaNit: string;
  precios?: { moneda: string; precio: number }[];
};

export const productosApi = {
  list: (empresaNit?: string) =>
    apiFetch<Producto[]>(
      empresaNit ? `/api/productos?empresaNit=${encodeURIComponent(empresaNit)}` : "/api/productos"
    ),
  create: (auth: AuthState, body: ProductoCreate) =>
    apiFetch<Producto>("/api/productos", { method: "POST", body: JSON.stringify(body), auth }),
};

