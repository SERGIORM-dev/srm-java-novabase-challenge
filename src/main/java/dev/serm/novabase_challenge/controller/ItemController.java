package dev.serm.novabase_challenge.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.serm.novabase_challenge.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	/**
	 * Returns the titles of all items whose average rating is lower than the
	 * given rating.
	 *
	 * @param rating the upper bound (exclusive) for the average rating
	 * @return the matching item titles, as JSON encoded in UTF-8
	 */
	@Operation(summary = "Títulos de artículos",
			description = "Devuelve los títulos de los artículos cuya calificación promedio "
					+ "es menor al valor indicado. Los artículos sin reseñas cuentan como 0. "
					+ "Requiere autenticación con token JWT.")
	@GetMapping(value = "/titles", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
	public List<String> getTitles(
			@Parameter(description = "Límite superior (excluyente) para la calificación promedio. "
					+ "Debe ser un número mayor o igual a 0.")
			@RequestParam @NotNull @PositiveOrZero Double rating) {
		return itemService.getTitles(rating);
	}

}
