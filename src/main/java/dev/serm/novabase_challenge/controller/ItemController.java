package dev.serm.novabase_challenge.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.serm.novabase_challenge.service.ItemService;
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
	@GetMapping(value = "/titles", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
	public List<String> getTitles(@RequestParam @NotNull @PositiveOrZero Double rating) {
		return itemService.getTitles(rating);
	}

}
