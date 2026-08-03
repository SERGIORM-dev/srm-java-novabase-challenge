package dev.serm.novabase_challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.serm.novabase_challenge.dao.ItemRepository;
import dev.serm.novabase_challenge.model.Item;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private ItemService itemService;

	@Test
	void getTitles_mapsRepositoryResultsToTitlesOnly() {
		Item item1 = new Item("Platinum Rewards Card", "desc");
		Item item2 = new Item("Cashback Plus Card", "desc");
		when(itemRepository.findItemsWithAverageRatingLowerThan(3.0))
				.thenReturn(List.of(item1, item2));

		List<String> titles = itemService.getTitles(3.0);

		assertThat(titles).containsExactly("Platinum Rewards Card", "Cashback Plus Card");
	}

	@Test
	void getTitles_returnsEmptyListWhenRepositoryReturnsNoItems() {
		when(itemRepository.findItemsWithAverageRatingLowerThan(0.0))
				.thenReturn(List.of());

		List<String> titles = itemService.getTitles(0.0);

		assertThat(titles).isEmpty();
	}

	@Test
	void getTitles_delegatesRatingParameterToRepository() {
		when(itemRepository.findItemsWithAverageRatingLowerThan(4.5))
				.thenReturn(List.of(new Item("Secured Credit Card", "desc")));

		List<String> titles = itemService.getTitles(4.5);

		assertThat(titles).containsExactly("Secured Credit Card");
	}

}
