package com.diegoabarajas.pruebatecnica.adapters.in.rest.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro transversal de observabilidad:
 * - Asegura que cada request tenga un correlationId.
 * - Lo expone al cliente en el header {@value #HEADER_NAME}.
 * - Lo deja en MDC (Mapped Diagnostic Context) para que aparezca en logs.
 *
 * <p>Convención:
 * - Si el cliente envía {@value #HEADER_NAME} o {@value #ALT_HEADER_NAME}, lo respetamos.
 * - Si no, generamos un UUID.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

	public static final String HEADER_NAME = "X-Correlation-Id";
	public static final String ALT_HEADER_NAME = "X-Request-Id";
	public static final String MDC_KEY = "correlationId";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String incoming = headerValue(request, HEADER_NAME);
		if (incoming == null || incoming.isBlank()) {
			incoming = headerValue(request, ALT_HEADER_NAME);
		}
		String correlationId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;

		MDC.put(MDC_KEY, correlationId);
		response.setHeader(HEADER_NAME, correlationId);
		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(MDC_KEY);
		}
	}

	private static String headerValue(HttpServletRequest req, String header) {
		String v = req.getHeader(header);
		return v == null ? null : v.trim();
	}
}

