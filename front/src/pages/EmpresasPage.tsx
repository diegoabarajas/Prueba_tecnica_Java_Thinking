import React from "react";
import {
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import AddIcon from "@mui/icons-material/Add";
import { useAuth } from "../auth/AuthContext";
import { empresasApi, type Empresa, type EmpresaCreate } from "../api/empresas";
import { ApiError } from "../api/http";
import { FeedbackSnackbar, type Feedback } from "../components/FeedbackSnackbar";

type FormState = EmpresaCreate;

export function EmpresasPage() {
  const { auth, isAdmin } = useAuth();
  const [items, setItems] = React.useState<Empresa[]>([]);
  const [loading, setLoading] = React.useState(true);
  const [err, setErr] = React.useState<string | null>(null);
  const [feedback, setFeedback] = React.useState<Feedback>({ open: false, severity: "info", message: "" });

  const [openForm, setOpenForm] = React.useState(false);
  const [editingNit, setEditingNit] = React.useState<string | null>(null);
  const [form, setForm] = React.useState<FormState>({ nit: "", nombre: "", direccion: "", telefono: "" });

  const load = React.useCallback(async () => {
    setLoading(true);
    setErr(null);
    try {
      const data = await empresasApi.list();
      setItems(data);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Error cargando empresas");
    } finally {
      setLoading(false);
    }
  }, []);

  React.useEffect(() => {
    void load();
  }, [load]);

  const openCreate = () => {
    setEditingNit(null);
    setForm({ nit: "", nombre: "", direccion: "", telefono: "" });
    setOpenForm(true);
  };

  const openEdit = (e: Empresa) => {
    setEditingNit(e.nit);
    setForm({ nit: e.nit, nombre: e.nombre, direccion: e.direccion ?? "", telefono: e.telefono ?? "" });
    setOpenForm(true);
  };

  const onSave = async () => {
    try {
      if (!isAdmin) return;
      if (editingNit) {
        await empresasApi.update(auth, editingNit, form);
        setFeedback({ open: true, severity: "success", message: "Empresa actualizada" });
      } else {
        await empresasApi.create(auth, form);
        setFeedback({ open: true, severity: "success", message: "Empresa creada" });
      }
      setOpenForm(false);
      await load();
    } catch (e) {
      const msg =
        e instanceof ApiError && e.body && typeof e.body === "object" && "message" in (e.body as any)
          ? String((e.body as any).message)
          : e instanceof Error
            ? e.message
            : "Error guardando empresa";
      setFeedback({ open: true, severity: "error", message: msg });
    }
  };

  const onDelete = async (nit: string) => {
    if (!isAdmin) return;
    const ok = window.confirm("¿Eliminar esta empresa? Esta acción no se puede deshacer.");
    if (!ok) return;
    try {
      await empresasApi.remove(auth, nit);
      setFeedback({ open: true, severity: "success", message: "Empresa eliminada" });
      await load();
    } catch (e) {
      setFeedback({ open: true, severity: "error", message: e instanceof Error ? e.message : "Error eliminando" });
    }
  };

  return (
    <Stack spacing={2.5}>
      <Stack direction={{ xs: "column", sm: "row" }} spacing={1} alignItems={{ sm: "center" }}>
        <Box sx={{ flex: 1 }}>
          <Typography variant="h4">Empresas</Typography>
          <Typography variant="body1" sx={{ color: "text.secondary" }}>
            Lista de empresas registradas. {isAdmin ? "Puedes crear/editar/eliminar." : "Modo lectura (Externo)."}
          </Typography>
        </Box>
        {isAdmin && (
          <Button startIcon={<AddIcon />} variant="contained" onClick={openCreate}>
            Nueva empresa
          </Button>
        )}
      </Stack>

      <Card>
        <CardContent>
          {loading ? (
            <Stack alignItems="center" sx={{ py: 4 }}>
              <CircularProgress />
              <Typography variant="body2" sx={{ mt: 1, color: "text.secondary" }}>
                Cargando...
              </Typography>
            </Stack>
          ) : err ? (
            <Typography color="error">{err}</Typography>
          ) : items.length === 0 ? (
            <Stack spacing={1} sx={{ py: 2 }}>
              <Typography variant="h6">No hay empresas</Typography>
              <Typography variant="body2" sx={{ color: "text.secondary" }}>
                {isAdmin ? "Crea la primera empresa para empezar." : "Aún no hay datos para mostrar."}
              </Typography>
              {isAdmin && (
                <Box>
                  <Button variant="contained" onClick={openCreate}>
                    Crear empresa
                  </Button>
                </Box>
              )}
            </Stack>
          ) : (
            <Table size="small" aria-label="Tabla de empresas">
              <TableHead>
                <TableRow>
                  <TableCell>NIT_empresa</TableCell>
                  <TableCell>Nombre</TableCell>
                  <TableCell>Dirección</TableCell>
                  <TableCell>Teléfono</TableCell>
                  {isAdmin && <TableCell align="right">Acciones</TableCell>}
                </TableRow>
              </TableHead>
              <TableBody>
                {items.map((e) => (
                  <TableRow key={e.nit} hover>
                    <TableCell>{e.nit}</TableCell>
                    <TableCell sx={{ fontWeight: 600 }}>{e.nombre}</TableCell>
                    <TableCell>{e.direccion ?? ""}</TableCell>
                    <TableCell>{e.telefono ?? ""}</TableCell>
                    {isAdmin && (
                      <TableCell align="right">
                        <IconButton aria-label="Editar" onClick={() => openEdit(e)}>
                          <EditIcon />
                        </IconButton>
                        <IconButton aria-label="Eliminar" onClick={() => onDelete(e.nit)}>
                          <DeleteIcon />
                        </IconButton>
                      </TableCell>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      <Dialog open={openForm} onClose={() => setOpenForm(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editingNit ? "Editar empresa" : "Nueva empresa"}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            <TextField
              label="NIT"
              value={form.nit}
              onChange={(ev) => setForm((s) => ({ ...s, nit: ev.target.value }))}
              disabled={Boolean(editingNit)}
              helperText={editingNit ? "No se puede cambiar el NIT." : "Identificador único."}
            />
            <TextField
              label="Nombre"
              value={form.nombre}
              onChange={(ev) => setForm((s) => ({ ...s, nombre: ev.target.value }))}
              required
            />
            <TextField
              label="Dirección"
              value={form.direccion ?? ""}
              onChange={(ev) => setForm((s) => ({ ...s, direccion: ev.target.value }))}
            />
            <TextField
              label="Teléfono"
              value={form.telefono ?? ""}
              onChange={(ev) => setForm((s) => ({ ...s, telefono: ev.target.value }))}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenForm(false)}>Cancelar</Button>
          <Button onClick={onSave} variant="contained" disabled={!form.nit || !form.nombre}>
            Guardar
          </Button>
        </DialogActions>
      </Dialog>

      <FeedbackSnackbar feedback={feedback} onClose={() => setFeedback((f) => ({ ...f, open: false }))} />
    </Stack>
  );
}

