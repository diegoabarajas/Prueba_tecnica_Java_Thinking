import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
  },
  build: {
    // Optimizaciones de build para reducir tamaño del bundle
    minify: "esbuild", // Más rápido que terser
    sourcemap: false, // Deshabilitar en producción para reducir tamaño
    rollupOptions: {
      output: {
        // Code splitting manual: separar vendor chunks
        manualChunks: {
          // Separar React y React DOM
          "react-vendor": ["react", "react-dom", "react-router-dom"],
          // Separar MUI (es el más pesado)
          "mui-vendor": [
            "@mui/material",
            "@mui/icons-material",
            "@emotion/react",
            "@emotion/styled",
          ],
          // Separar librerías de formularios
          "form-vendor": ["react-hook-form", "zod", "@hookform/resolvers"],
        },
      },
    },
    // Chunk size warning limit (aumentar para evitar warnings en build)
    chunkSizeWarningLimit: 1000,
  },
});

