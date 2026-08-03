package dev.serm.novabase_challenge.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@Schema(description = "Nombre de usuario registrado (alice, bob o carol en los datos de ejemplo).", example = "alice")
		@NotBlank String username,

		@Schema(description = "Contraseña del usuario.", example = "password123")
		@NotBlank String password) {
}
