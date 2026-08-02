package dev.serm.novabase_challenge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@NotNull
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@Setter
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Setter
	@NotNull
	@Min(1)
	@Max(5)
	@Column(nullable = false)
	private Integer rating;

	@Setter
	@Column
	private String comment;

	public Review(Item item, User user, Integer rating, String comment) {
		this.item = item;
		this.user = user;
		this.rating = rating;
		this.comment = comment;
	}

}
