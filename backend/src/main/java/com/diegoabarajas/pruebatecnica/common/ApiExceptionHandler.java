package com.diegoabarajas.pruebatecnica.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Centraliza el manejo de errores para que la API responda de forma consistente.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			// si hay múltiples errores por campo, nos quedamos con el primero
			fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
		}
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Validación fallida",
				req.getRequestURI(),
				fieldErrors
		);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				ex.getReason(),
				req.getRequestURI(),
				null
		);
		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
	public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex, HttpServletRequest req) {
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Solicitud inválida",
				req.getRequestURI(),
				null
		);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public ResponseEntity<ApiErrorResponse> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest req) {
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				HttpStatus.NOT_ACCEPTABLE.value(),
				HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
				"No aceptable",
				req.getRequestURI(),
				null
		);
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				"Error interno",
				req.getRequestURI(),
				null
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}
}

