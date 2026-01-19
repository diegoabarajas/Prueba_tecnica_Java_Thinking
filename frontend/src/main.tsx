import React, { Suspense, lazy } from "react";
import ReactDOM from "react-dom/client";
import { createBrowserRouter, Navigate, RouterProvider } from "react-router-dom";
import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import { CircularProgress, Box } from "@mui/material";
import { theme } from "./theme/theme";
import { AuthProvider } from "./auth/AuthContext";
import { RequireAuth } from "./routes/RequireAuth";
import { AppShell } from "./components/AppShell";
import "./style.css";

// Code splitting: cargar páginas bajo demanda para reducir bundle inicial
const LoginPage = lazy(() => import("./pages/LoginPage").then(m => ({ default: m.LoginPage })));
const EmpresasPage = lazy(() => import("./pages/EmpresasPage").then(m => ({ default: m.EmpresasPage })));
const ProductosPage = lazy(() => import("./pages/ProductosPage").then(m => ({ default: m.ProductosPage })));
const InventarioPage = lazy(() => import("./pages/InventarioPage").then(m => ({ default: m.InventarioPage })));

// Componente de carga para Suspense
const PageLoader = () => (
  <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "400px" }}>
    <CircularProgress />
  </Box>
);

const router = createBrowserRouter([
  {
    path: "/",
    element: <AppShell />,
    children: [
      { index: true, element: <Navigate to="/empresas" replace /> },
      {
        path: "login",
        element: (
          <Suspense fallback={<PageLoader />}>
            <LoginPage />
          </Suspense>
        ),
      },
      {
        path: "empresas",
        element: (
          <RequireAuth>
            <Suspense fallback={<PageLoader />}>
              <EmpresasPage />
            </Suspense>
          </RequireAuth>
        ),
      },
      {
        path: "productos",
        element: (
          <RequireAuth>
            <Suspense fallback={<PageLoader />}>
              <ProductosPage />
            </Suspense>
          </RequireAuth>
        ),
      },
      {
        path: "inventario",
        element: (
          <RequireAuth>
            <Suspense fallback={<PageLoader />}>
              <InventarioPage />
            </Suspense>
          </RequireAuth>
        ),
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>
);

