package dev.serm.novabase_challenge.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import dev.serm.novabase_challenge.model.Item;
import dev.serm.novabase_challenge.model.Review;
import dev.serm.novabase_challenge.model.User;

@DataJpaTest
class ItemRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ItemRepository itemRepository;

	@Test
	void findItemsWithAverageRatingLowerThan_excludesItemsWithHigherAverage() {
		User user = persistUser("alice");

		Item highRated = persistItem("High Rated Card", "desc");
		persistReview(highRated, user, 5);
		persistReview(highRated, user, 4);

		Item lowRated = persistItem("Low Rated Card", "desc");
		persistReview(lowRated, user, 1);
		persistReview(lowRated, user, 2);

		entityManager.flush();

		var result = itemRepository.findItemsWithAverageRatingLowerThan(3.0);

		assertThat(result)
				.extracting(Item::getTitle)
				.containsExactly("Low Rated Card");
	}

	@Test
	void findItemsWithAverageRatingLowerThan_treatsItemsWithoutReviewsAsZero() {
		persistItem("Item Without Reviews", "desc");

		entityManager.flush();

		var result = itemRepository.findItemsWithAverageRatingLowerThan(0.5);

		assertThat(result)
				.extracting(Item::getTitle)
				.containsExactly("Item Without Reviews");
	}

	@Test
	void findItemsWithAverageRatingLowerThan_excludesItemsWithoutReviewsWhenRatingIsZero() {
		persistItem("Item Without Reviews", "desc");

		entityManager.flush();

		var result = itemRepository.findItemsWithAverageRatingLowerThan(0.0);

		assertThat(result).isEmpty();
	}

	@Test
	void findItemsWithAverageRatingLowerThan_returnsAllWhenRatingIsHighEnough() {
		User user = persistUser("bob");
		Item item = persistItem("Some Card", "desc");
		persistReview(item, user, 3);

		entityManager.flush();

		var result = itemRepository.findItemsWithAverageRatingLowerThan(5.0);

		assertThat(result)
				.extracting(Item::getTitle)
				.containsExactly("Some Card");
	}

	private User persistUser(String username) {
		User user = new User(username, username + "@example.com");
		return entityManager.persist(user);
	}

	private Item persistItem(String title, String description) {
		Item item = new Item(title, description);
		return entityManager.persist(item);
	}

	private Review persistReview(Item item, User user, Integer rating) {
		Review review = new Review(item, user, rating, null);
		return entityManager.persist(review);
	}

}
