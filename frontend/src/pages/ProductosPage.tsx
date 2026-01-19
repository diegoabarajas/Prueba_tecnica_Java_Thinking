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
import AddIcon from "@mui/icons-material/Add";
import { useAuth } from "../auth/AuthContext";
import { productosApi, type ProductoCreate, type Producto } from "../api/productos";
import { FeedbackSnackbar, type Feedback } from "../components/FeedbackSnackbar";

export function ProductosPage() {
  const { auth, isAdmin } = useAuth();
  const [empresaNit, setEmpresaNit] = React.useState("900123456");
  const [items, setItems] = React.useState<Producto[]>([]);
  const [loading, setLoading] = React.useState(false);
  const [feedback, setFeedback] = React.useState<Feedback>({ open: false, severity: "info", message: "" });

  const [form, setForm] = React.useState<ProductoCreate>({
    codigo: "",
    nombre: "",
    caracteristicas: "",
    empresaNit: "900123456",
    precios: [],
  });

  const [moneda, setMoneda] = React.useState<"COP" | "USD" | "UE">("COP");
  const [precio, setPrecio] = React.useState<string>("");

  const load = React.useCallback(async () => {
    setLoading(true);
    try {
      const data = await productosApi.list(empresaNit);
      setItems(data);
    } catch (e) {
      setFeedback({ open: true, severity: "error", message: e instanceof Error ? e.message : "Error cargando productos" });
    } finally {
      setLoading(false);
    }
  }, [empresaNit]);

  React.useEffect(() => {
    void load();
  }, [load]);

  const onCreate = async () => {
    try {
      if (!isAdmin) return;
      await productosApi.create(auth, { ...form, empresaNit });
      setFeedback({ open: true, severity: "success", message: "Producto creado" });
      setForm((s) => ({ ...s, codigo: "", nombre: "", caracteristicas: "" }));
      setMoneda("COP");
      setPrecio("");
      await load();
    } catch (e) {
      setFeedback({ open: true, severity: "error", message: e instanceof Error ? e.message : "Error creando producto" });
    }
  };

  const onAddPrecio = () => {
    if (!isAdmin) return;
    const v = Number(precio);
    if (!precio.trim() || !Number.isFinite(v) || v <= 0) {
      setFeedback({ open: true, severity: "error", message: "Precio inválido" });
      return;
    }
    setForm((s) => {
      const prev = s.precios ?? [];
      // Si ya existe esa moneda, la reemplazamos manteniendo el orden original
      const idx = prev.findIndex((p) => p.moneda === moneda);
      if (idx >= 0) {
        const next = [...prev];
        next[idx] = { moneda, precio: v };
        return { ...s, precios: next };
      }
      return { ...s, precios: [...prev, { moneda, precio: v }] };
    });
    setPrecio("");
  };

  return (
    <Stack spacing={2.5}>
      <Box>
        <Typography variant="h4">Productos</Typography>
        <Typography variant="body1" sx={{ color: "text.secondary" }}>
          Crea y lista productos por empresa.
        </Typography>
      </Box>

      <Card>
        <CardContent>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems={{ sm: "center" }}>
            <TextField
              label="Empresa NIT"
              value={empresaNit}
              onChange={(e) => {
                setEmpresaNit(e.target.value);
                setForm((s) => ({ ...s, empresaNit: e.target.value }));
              }}
              helperText="Filtra por empresa"
              sx={{ minWidth: 240 }}
            />
            <Button onClick={load} disabled={loading}>
              Refrescar
            </Button>
            <Box sx={{ flex: 1 }} />
          </Stack>

          {isAdmin && (
            <Stack spacing={2} sx={{ mt: 2 }}>
              <Typography variant="h6">Nuevo producto</Typography>
              <Stack direction={{ xs: "column", md: "row" }} spacing={2}>
                <TextField
                  label="Código"
                  value={form.codigo}
                  onChange={(e) => setForm((s) => ({ ...s, codigo: e.target.value }))}
                  sx={{ flex: 1 }}
                />
                <TextField
                  label="Nombre"
                  value={form.nombre}
                  onChange={(e) => setForm((s) => ({ ...s, nombre: e.target.value }))}
                  sx={{ flex: 2 }}
                />
              </Stack>
              <TextField
                label="Características"
                value={form.caracteristicas ?? ""}
                onChange={(e) => setForm((s) => ({ ...s, caracteristicas: e.target.value }))}
                multiline
                minRows={2}
              />
              <Stack direction={{ xs: "column", md: "row" }} spacing={2}>
                <TextField
                  label="Moneda"
                  select
                  SelectProps={{ native: true }}
                  value={moneda}
                  onChange={(e) => setMoneda(e.target.value as any)}
                  sx={{ width: 180 }}
                >
                  <option value="COP">COP</option>
                  <option value="USD">USD</option>
                  <option value="UE">UE</option>
                </TextField>
                <TextField
                  label="Precio"
                  value={precio}
                  onChange={(e) => setPrecio(e.target.value)}
                  inputMode="decimal"
                  sx={{ flex: 1, minWidth: 200 }}
                />
                <Button variant="outlined" onClick={onAddPrecio} startIcon={<AddIcon />}>
                  Agregar
                </Button>
              </Stack>
              <TextField
                label="Precios agregados"
                value={(form.precios ?? []).map((p) => `${p.moneda} ${p.precio}`).join("\n")}
                multiline
                minRows={3}
                InputProps={{ readOnly: true }}
                helperText="Formato: COP 1000 (una línea por moneda)"
              />
              <Box>
                <Button
                  startIcon={<AddIcon />}
                  variant="contained"
                  onClick={onCreate}
                  disabled={!form.codigo || !form.nombre || !empresaNit}
                >
                  Crear producto
                </Button>
              </Box>
            </Stack>
          )}

          <Box sx={{ mt: 3 }}>
            <Typography variant="h6" sx={{ mb: 1 }}>
              Listado
            </Typography>
            {loading ? (
              <Stack alignItems="center" sx={{ py: 4 }}>
                <CircularProgress />
              </Stack>
            ) : items.length === 0 ? (
              <Typography variant="body2" sx={{ color: "text.secondary" }}>
                No hay productos para esta empresa.
              </Typography>
            ) : (
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Código</TableCell>
                    <TableCell>Nombre</TableCell>
                    <TableCell>Características</TableCell>
                    <TableCell>Precios</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {items.map((p) => (
                    <TableRow key={p.codigo} hover>
                      <TableCell sx={{ fontWeight: 700 }}>{p.codigo}</TableCell>
                      <TableCell>{p.nombre}</TableCell>
                      <TableCell>{p.caracteristicas ?? ""}</TableCell>
                      <TableCell>
                        {(p.precios ?? []).length === 0 ? (
                          "-"
                        ) : (
                          <Stack spacing={0.25}>
                            {(p.precios ?? []).map((x) => (
                              <Typography key={x.moneda} variant="body2">
                                {x.moneda} {x.precio}
                              </Typography>
                            ))}
                          </Stack>
                        )}
                      </TableCell>
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

