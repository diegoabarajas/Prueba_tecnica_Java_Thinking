package com.diegoabarajas.pruebatecnica.core.ports.out.email;

/**
 * Puerto de salida (outbound port) para envío de emails.
 *
 * <p>El core define el contrato; la infraestructura (AWS SES, SMTP, etc.) lo implementa
 * mediante un adaptador outbound.
 */
public interface EmailSenderPort {
	/**
	 * Envía un email con un PDF adjunto.
	 *
	 * <p>Nota: la implementación decide el proveedor (por ejemplo AWS SES con Raw Email MIME).
	 */
	void sendPdf(String toEmail, String subject, String message, String filename, byte[] pdfBytes);
}

