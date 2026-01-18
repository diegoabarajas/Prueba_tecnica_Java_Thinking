import React from "react";
import {
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Divider,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { FeedbackSnackbar, type Feedback } from "../components/FeedbackSnackbar";

const schema = z.object({
  email: z.string().email("Correo inválido"),
  password: z.string().min(1, "La contraseña es requerida"),
  role: z.enum(["ADMIN", "EXTERNO"]),
});

type FormValues = z.infer<typeof schema>;

export function LoginPage() {
  const navigate = useNavigate();
  const { loginBasic } = useAuth();
  const [feedback, setFeedback] = React.useState<Feedback>({
    open: false,
    severity: "info",
    message: "",
  });

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { role: "ADMIN" },
  });

  const onSubmit = async (v: FormValues) => {
    // En esta fase usamos Basic Auth (rápido). Más adelante migramos a JWT.
    loginBasic(v.email, v.password, v.role);
    setFeedback({ open: true, severity: "success", message: "Sesión iniciada" });
    navigate("/empresas", { replace: true });
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ pt: 4 }}>
        <Typography variant="h4" sx={{ mb: 1 }}>
          Iniciar sesión
        </Typography>
        <Typography variant="body1" sx={{ color: "text.secondary", mb: 3 }}>
          Usa tus credenciales para acceder. Admin habilita acciones de creación/edición/eliminación.
        </Typography>

        <Card>
          <CardContent>
            <Stack component="form" spacing={2.2} onSubmit={handleSubmit(onSubmit)} noValidate>
              <TextField
                label="Correo"
                type="email"
                autoComplete="email"
                error={Boolean(errors.email)}
                helperText={errors.email?.message}
                {...register("email")}
              />
              <TextField
                label="Contraseña"
                type="password"
                autoComplete="current-password"
                error={Boolean(errors.password)}
                helperText={errors.password?.message}
                {...register("password")}
              />

              <Divider />

              <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5}>
                <TextField
                  label="Rol"
                  select
                  SelectProps={{ native: true }}
                  defaultValue="ADMIN"
                  helperText="En esta fase, el rol se usa para mostrar/ocultar acciones en UI."
                  {...register("role")}
                >
                  <option value="ADMIN">ADMIN</option>
                  <option value="EXTERNO">EXTERNO</option>
                </TextField>
                <Box sx={{ flex: 1 }} />
                <Button type="submit" variant="contained" disabled={isSubmitting} sx={{ minWidth: 160 }}>
                  {isSubmitting ? "Ingresando..." : "Ingresar"}
                </Button>
              </Stack>

              <Typography variant="caption" sx={{ color: "text.secondary" }}>
                Tip: credenciales dev (backend): admin@local.test / ChangeMe123!
              </Typography>
            </Stack>
          </CardContent>
        </Card>
      </Box>

      <FeedbackSnackbar feedback={feedback} onClose={() => setFeedback((f) => ({ ...f, open: false }))} />
    </Container>
  );
}

