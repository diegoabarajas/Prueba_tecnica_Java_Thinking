import {
  AppBar,
  Box,
  Button,
  Container,
  IconButton,
  Toolbar,
  Typography,
} from "@mui/material";
import LogoutIcon from "@mui/icons-material/Logout";
import { NavLink, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

const linkStyle = ({ isActive }: { isActive: boolean }) => ({
  color: "inherit",
  textDecoration: "none",
  opacity: isActive ? 1 : 0.75,
  fontWeight: isActive ? 700 : 500,
});

export function AppShell() {
  const { isAuthed, isAdmin, auth, logout } = useAuth();
  const { pathname } = useLocation();
  const isLoginRoute = pathname === "/login";

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "background.default" }}>
      <AppBar position="sticky" elevation={0} color="default">
        <Toolbar sx={{ gap: 2 }}>
          <Typography variant="h6" sx={{ fontWeight: 800 }}>
            Prueba Técnica
          </Typography>

          {isAuthed && (
            <Box sx={{ display: "flex", gap: 1, flex: 1 }}>
              <Button component={NavLink} to="/empresas" color="inherit" sx={{ textTransform: "none" }} style={linkStyle as any}>
                Empresas
              </Button>
              <Button component={NavLink} to="/productos" color="inherit" sx={{ textTransform: "none" }} style={linkStyle as any}>
                Productos
              </Button>
              <Button component={NavLink} to="/inventario" color="inherit" sx={{ textTransform: "none" }} style={linkStyle as any}>
                Inventario
              </Button>
            </Box>
          )}

          {isAuthed ? (
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <Typography variant="body2" sx={{ color: "text.secondary" }}>
                {auth.email} {isAdmin ? "(Admin)" : auth.role ? `(${auth.role})` : ""}
              </Typography>
              <IconButton aria-label="Cerrar sesión" onClick={logout}>
                <LogoutIcon />
              </IconButton>
            </Box>
          ) : isLoginRoute ? null : (
            <Button component={NavLink} to="/login" variant="contained">
              Ingresar
            </Button>
          )}
        </Toolbar>
      </AppBar>

      <Container sx={{ py: 3 }}>
        <Outlet />
      </Container>
    </Box>
  );
}

