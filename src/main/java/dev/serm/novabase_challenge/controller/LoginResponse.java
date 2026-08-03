package dev.serm.novabase_challenge.controller;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
		@Schema(description = "Token JWT de acceso, válido por 60 minutos. "
				+ "Enviar en el header Authorization: Bearer <token>.", example = "eyJhbGciOiJIUzM4NCJ9...")
		String token) {
}
