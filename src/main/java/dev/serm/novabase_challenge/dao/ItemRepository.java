package dev.serm.novabase_challenge.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.serm.novabase_challenge.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	@Query("""
			SELECT i FROM Item i
			LEFT JOIN i.reviews r
			GROUP BY i
			HAVING COALESCE(AVG(r.rating), 0) < :rating
			""")
	List<Item> findItemsWithAverageRatingLowerThan(@Param("rating") Double rating);

}
