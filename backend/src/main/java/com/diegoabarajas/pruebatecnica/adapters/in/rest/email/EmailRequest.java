package com.diegoabarajas.pruebatecnica.adapters.in.rest.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailRequest(
		@NotBlank @Size(max = 32) String empresaNit,
		@NotBlank @Email @Size(max = 255) String toEmail,
		@Size(max = 255) String subject,
		@Size(max = 2000) String message
) {
}

