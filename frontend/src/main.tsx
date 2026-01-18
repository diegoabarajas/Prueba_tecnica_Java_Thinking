import React from "react";
import ReactDOM from "react-dom/client";
import { createBrowserRouter, Navigate, RouterProvider } from "react-router-dom";
import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "./theme/theme";
import { AuthProvider } from "./auth/AuthContext";
import { AppShell } from "./components/AppShell";
import { RequireAuth } from "./routes/RequireAuth";
import { LoginPage } from "./pages/LoginPage";
import { EmpresasPage } from "./pages/EmpresasPage";
import { ProductosPage } from "./pages/ProductosPage";
import { InventarioPage } from "./pages/InventarioPage";
import "./style.css";

const router = createBrowserRouter([
  { path: "/", element: <Navigate to="/empresas" replace /> },
  { path: "/login", element: <LoginPage /> },
  {
    path: "/empresas",
    element: (
      <RequireAuth>
        <EmpresasPage />
      </RequireAuth>
    ),
  },
  {
    path: "/productos",
    element: (
      <RequireAuth>
        <ProductosPage />
      </RequireAuth>
    ),
  },
  {
    path: "/inventario",
    element: (
      <RequireAuth>
        <InventarioPage />
      </RequireAuth>
    ),
  },
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <AppShell>
          <RouterProvider router={router} />
        </AppShell>
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>
);

