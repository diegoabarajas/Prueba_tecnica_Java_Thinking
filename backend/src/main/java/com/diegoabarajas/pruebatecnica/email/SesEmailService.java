package com.diegoabarajas.pruebatecnica.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Envío de correos vía AWS SES usando Raw Email (MIME) para adjuntar PDF.
 *
 * Requiere variables de entorno:
 * - AWS_REGION
 * - AWS_SES_FROM (email verificado en SES)
 * - AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY (dev local) o roles (prod)
 */
@Service
public class SesEmailService {

	private final String fromEmail;
	private final Region region;

	public SesEmailService(
			@Value("${AWS_SES_FROM:}") String fromEmail,
			@Value("${AWS_REGION:}") String awsRegion
	) {
		this.fromEmail = fromEmail;
		this.region = awsRegion == null || awsRegion.isBlank() ? null : Region.of(awsRegion);
	}

	public void sendPdf(String toEmail, String subject, String message, String filename, byte[] pdfBytes) {
		if (fromEmail == null || fromEmail.isBlank()) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falta configurar AWS_SES_FROM");
		}
		if (region == null) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falta configurar AWS_REGION");
		}

		String subj = (subject == null || subject.isBlank()) ? "Inventario" : subject;
		String bodyText = (message == null || message.isBlank()) ? "Adjunto inventario en PDF." : message;

		String boundary = "----=_Part_" + UUID.randomUUID();
		String mime = buildMime(fromEmail, toEmail, subj, bodyText, filename, pdfBytes, boundary);

		try (SesClient ses = SesClient.builder().region(region).build()) {
			SendRawEmailRequest req = SendRawEmailRequest.builder()
					.rawMessage(RawMessage.builder().data(SdkBytes.fromByteArray(mime.getBytes(StandardCharsets.UTF_8))).build())
					.build();
			ses.sendRawEmail(req);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error enviando correo con SES: " + e.getMessage(), e);
		}
	}

	static String buildMime(
			String from,
			String to,
			String subject,
			String bodyText,
			String filename,
			byte[] pdfBytes,
			String boundary
	) {
		String encodedSubject = subject.replace("\r", "").replace("\n", "");
		String safeText = bodyText.replace("\r", "");

		String pdfB64 = Base64.getMimeEncoder(76, "\r\n".getBytes(StandardCharsets.UTF_8)).encodeToString(pdfBytes);

		return ""
				+ "From: " + from + "\r\n"
				+ "To: " + to + "\r\n"
				+ "Subject: " + encodedSubject + "\r\n"
				+ "MIME-Version: 1.0\r\n"
				+ "Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n"
				+ "\r\n"
				+ "--" + boundary + "\r\n"
				+ "Content-Type: text/plain; charset=UTF-8\r\n"
				+ "Content-Transfer-Encoding: 7bit\r\n"
				+ "\r\n"
				+ safeText + "\r\n"
				+ "\r\n"
				+ "--" + boundary + "\r\n"
				+ "Content-Type: application/pdf; name=\"" + filename + "\"\r\n"
				+ "Content-Description: " + filename + "\r\n"
				+ "Content-Disposition: attachment; filename=\"" + filename + "\"; size=" + pdfBytes.length + ";\r\n"
				+ "Content-Transfer-Encoding: base64\r\n"
				+ "\r\n"
				+ pdfB64 + "\r\n"
				+ "--" + boundary + "--\r\n";
	}
}

