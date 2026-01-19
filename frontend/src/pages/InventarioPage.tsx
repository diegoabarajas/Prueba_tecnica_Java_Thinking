import React from "react";
import {
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import EmailIcon from "@mui/icons-material/Email";
import { inventarioApi, type InventarioItem } from "../api/inventario";
import { empresasApi, type Empresa } from "../api/empresas";
import { useAuth } from "../auth/AuthContext";
import { FeedbackSnackbar, type Feedback } from "../components/FeedbackSnackbar";

export function InventarioPage() {
  const { auth, isAdmin } = useAuth();
  const [empresaNit, setEmpresaNit] = React.useState("");
  const [empresas, setEmpresas] = React.useState<Empresa[]>([]);
  const [empresasLoading, setEmpresasLoading] = React.useState(false);
  const [items, setItems] = React.useState<InventarioItem[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [feedback, setFeedback] = React.useState<Feedback>({ open: false, severity: "info", message: "" });

  const [toEmail, setToEmail] = React.useState("");

  const loadEmpresas = React.useCallback(async () => {
    setEmpresasLoading(true);
    try {
      const data = await empresasApi.list();
      setEmpresas(data);
      if (!empresaNit && data.length > 0) {
        setEmpresaNit(data[0].nit);
      }
    } catch (e) {
      setFeedback({ open: true, severity: "error", message: e instanceof Error ? e.message : "Error cargando empresas" });
    } finally {
      setEmpresasLoading(false);
    }
  }, [empresaNit]);

  const load = React.useCallback(async () => {
    setLoading(true);
    try {
      const data = await inventarioApi.list(empresaNit);
      setItems(data);
    } catch (e) {
      setFeedback({ open: true, severity: "error", message: e instanceof Error ? e.message : "Error cargando inventario" });
    } finally {
      setLoading(false);
    }
  }, [empresaNit]);

  React.useEffect(() => {
    void loadEmpresas();
  }, [loadEmpresas]);

  React.useEffect(() => {
    if (empresaNit) {
      void load();
    }
  }, [load, empresaNit]);

  const downloadPdf = async () => {
    try {
      const blob = await inventarioApi.downloadPdf(empresaNit);
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `inventario_${empresaNit}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
      setFeedback({ open: true, severity: "success", message: "PDF descargado" });
    } catch (e) {
      setFeedback({ open: true, severity: "error", message: e instanceof Error ? e.message : "No se pudo descargar el PDF" });
    }
  };

  const sendEmail = async () => {
    if (!isAdmin) return;
    try {
      await inventarioApi.sendEmail(auth, {
        empresaNit,
        toEmail,
        subject: "Inventario",
        message: "Adjunto el inventario en PDF.",
      });
      setFeedback({ open: true, severity: "success", message: "Solicitud de envío realizada" });
    } catch (e) {
      setFeedback({
        open: true,
        severity: "error",
        message: e instanceof Error ? e.message : "No se pudo enviar el correo",
      });
    }
  };

  return (
    <Stack spacing={2.5}>
      <Box>
        <Typography variant="h4">Inventario</Typography>
        <Typography variant="body1" sx={{ color: "text.secondary" }}>
          Productos por empresa, con descarga de PDF. El envío por correo requiere configuración AWS.
        </Typography>
      </Box>

      <Card>
        <CardContent>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems={{ sm: "center" }}>
            <TextField
              label="Empresa NIT"
              select
              SelectProps={{ native: true }}
              value={empresaNit}
              onChange={(e) => setEmpresaNit(e.target.value)}
              helperText={empresasLoading ? "Cargando empresas..." : "Selecciona una empresa"}
              sx={{ minWidth: 260 }}
            >
              {empresas.length === 0 ? (
                <option value="">No hay empresas</option>
              ) : (
                empresas.map((e) => (
                  <option key={e.nit} value={e.nit}>
                    {e.nit} - {e.nombre}
                  </option>
                ))
              )}
            </TextField>
            <Button onClick={load} disabled={loading || !empresaNit}>
              Refrescar
            </Button>
            <Box sx={{ flex: 1 }} />
            <Button startIcon={<DownloadIcon />} variant="outlined" onClick={downloadPdf} disabled={!empresaNit}>
              Descargar PDF
            </Button>
          </Stack>

          {isAdmin && (
            <Stack direction={{ xs: "column", md: "row" }} spacing={2} sx={{ mt: 2 }} alignItems={{ md: "center" }}>
              <TextField
                label="Enviar a (email)"
                value={toEmail}
                onChange={(e) => setToEmail(e.target.value)}
                placeholder="tucorreo@gmail.com"
                sx={{ flex: 1 }}
              />
              <Button
                startIcon={<EmailIcon />}
                variant="contained"
                onClick={sendEmail}
                disabled={!toEmail || !empresaNit}
              >
                Enviar PDF
              </Button>
              <Typography variant="caption" sx={{ color: "text.secondary" }}>
                Si AWS no está configurado, verás un error (esperado).
              </Typography>
            </Stack>
          )}

          <Box sx={{ mt: 3 }}>
            {loading ? (
              <Stack alignItems="center" sx={{ py: 4 }}>
                <CircularProgress />
              </Stack>
            ) : items.length === 0 ? (
              <Typography variant="body2" sx={{ color: "text.secondary" }}>
                No hay inventario para esta empresa.
              </Typography>
            ) : (
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Código</TableCell>
                    <TableCell>Nombre</TableCell>
                    <TableCell>Características</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {items.map((it) => (
                    <TableRow key={it.productoCodigo} hover>
                      <TableCell sx={{ fontWeight: 700 }}>{it.productoCodigo}</TableCell>
                      <TableCell>{it.productoNombre}</TableCell>
                      <TableCell>{it.caracteristicas ?? ""}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </Box>
        </CardContent>
      </Card>

      <FeedbackSnackbar feedback={feedback} onClose={() => setFeedback((f) => ({ ...f, open: false }))} />
    </Stack>
  );
}

