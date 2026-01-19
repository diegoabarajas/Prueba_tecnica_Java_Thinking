package com.diegoabarajas.pruebatecnica.adapters.in.rest.error;

import java.time.Instant;
import java.util.Map;

/**
 * Respuesta de error consistente para la API.
 * Mantiene el payload simple y útil para frontend y debugging.
 */
public record ApiErrorResponse(
		Instant timestamp,
		String correlationId,
		int status,
		String errorCode,
		String error,
		String message,
		String path,
		Map<String, String> fieldErrors
) {
}

