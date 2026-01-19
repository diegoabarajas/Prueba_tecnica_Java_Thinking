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
import { authApi } from "../api/auth";
import { ApiError } from "../api/http";

const schema = z.object({
  email: z.string().email("Correo inválido"),
  password: z.string().min(1, "La contraseña es requerida"),
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
    defaultValues: { email: "", password: "" },
  });

  const onSubmit = async (v: FormValues) => {
    // Validamos credenciales contra backend (evita “entrar” con credenciales incorrectas).
    const basicToken = btoa(`${v.email}:${v.password}`);
    try {
      const me = await authApi.me(basicToken);
      const role = me.role === "ADMIN" ? "ADMIN" : "EXTERNO";
      loginBasic(v.email, v.password, role);
      setFeedback({ open: true, severity: "success", message: "Sesión iniciada" });
      navigate("/empresas", { replace: true });
    } catch (e) {
      const status = e instanceof ApiError ? e.status : undefined;
      setFeedback({
        open: true,
        severity: "error",
        message: status === 401 ? "Credenciales inválidas" : e instanceof Error ? e.message : "No se pudo iniciar sesión",
      });
    }
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ pt: 4 }}>
        <Typography variant="h4" sx={{ mb: 1 }}>
          Iniciar sesión
        </Typography>
        <Typography variant="body1" sx={{ color: "text.secondary", mb: 3 }}>
          Bienvenido, ingresa tus credenciales.
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
                <Box sx={{ flex: 1 }} />
                <Button type="submit" variant="contained" disabled={isSubmitting} sx={{ minWidth: 160 }}>
                  {isSubmitting ? "Ingresando..." : "Ingresar"}
                </Button>
              </Stack>
            </Stack>
          </CardContent>
        </Card>
      </Box>

      <FeedbackSnackbar feedback={feedback} onClose={() => setFeedback((f) => ({ ...f, open: false }))} />
    </Container>
  );
}

