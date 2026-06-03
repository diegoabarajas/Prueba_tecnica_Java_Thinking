import { Alert, Snackbar } from "@mui/material";

export type Feedback = {
  open: boolean;
  severity: "success" | "error" | "info" | "warning";
  message: string;
};

export function FeedbackSnackbar({
  feedback,
  onClose,
}: {
  feedback: Feedback;
  onClose: () => void;
}) {
  return (
    <Snackbar
      open={feedback.open}
      autoHideDuration={3500}
      onClose={onClose}
      anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
    >
      <Alert onClose={onClose} severity={feedback.severity} variant="filled" sx={{ width: "100%" }}>
        {feedback.message}
      </Alert>
    </Snackbar>
  );
}

