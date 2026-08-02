package dev.serm.novabase_challenge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.serm.novabase_challenge.dao.ItemRepository;
import dev.serm.novabase_challenge.model.Item;

@Service
public class ItemService {

	private final ItemRepository itemRepository;

	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	/**
	 * Retrieves the titles of all items whose average rating is lower than the
	 * given rating.
	 *
	 * @param rating the upper bound (exclusive) for the average rating
	 * @return the titles of the matching items
	 */
	public List<String> getTitles(Double rating) {
		return itemRepository.findItemsWithAverageRatingLowerThan(rating).stream()
				.map(Item::getTitle)
				.toList();
	}

}
