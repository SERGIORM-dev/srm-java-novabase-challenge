package dev.serm.novabase_challenge.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.serm.novabase_challenge.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
