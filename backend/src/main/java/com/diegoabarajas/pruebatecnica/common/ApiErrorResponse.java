package com.diegoabarajas.pruebatecnica.common;

import java.time.Instant;
import java.util.Map;

/**
 * Respuesta de error consistente para la API.
 * Mantiene el payload simple y útil para frontend y debugging.
 */
public record ApiErrorResponse(
		Instant timestamp,
		int status,
		String error,
		String message,
		String path,
		Map<String, String> fieldErrors
) {
}

