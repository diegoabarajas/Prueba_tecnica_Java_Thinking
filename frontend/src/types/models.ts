// Empresas
export interface Empresa {
  nit: string;
  nombre: string;
  direccion: string;
  telefono: string;
}

// Productos
export interface ProductoPrecio {
  moneda: string;
  precio: number;
}

export interface Producto {
  codigo: string;
  nombre: string;
  caracteristicas: string;
  empresaNit: string;
  precios: ProductoPrecio[];
}

// Categorías
export interface Categoria {
  id: number;
  nombre: string;
}

// Clientes
export interface Cliente {
  id: number;
  correo: string;
  nombre: string;
}

// Órdenes
export interface OrdenItem {
  productoCodigo: string;
  cantidad: number;
}

export interface Orden {
  id: number;
  clienteId: number;
  fechaCreacion: string;
  items: OrdenItem[];
}

// Inventario
export interface InventarioItem {
  empresaNit: string;
  productoCodigo: string;
  productoNombre: string;
  caracteristicas: string;
}

// Auth
export interface AuthResponse {
  email: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}
