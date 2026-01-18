# UX/UI (decisiones)

## Principios

- **Simple y empresarial**: priorizar claridad, no “decoración”.
- **Jerarquía visual**: títulos claros, acción primaria destacada, acciones destructivas confirmadas.
- **Consistencia**: mismo layout, mismos espaciados, mismos componentes.
- **Accesibilidad básica**: contraste, labels, focus visible, navegación por teclado.
- **Estados**: loading / empty / error siempre visibles y con copy útil.

## Patrones aplicados

- **Layout** con AppBar + navegación y contenido centrado.
- **Formularios** con labels persistentes, validación y mensajes por campo.
- **Tablas** con:
  - Empty state (“No hay datos…”) + CTA si aplica
  - Acciones por fila (editar/eliminar) con confirmación
- **Feedback** con Snackbars (éxito/error) y spinners en botones.

