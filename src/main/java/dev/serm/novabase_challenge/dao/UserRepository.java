package dev.serm.novabase_challenge.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.serm.novabase_challenge.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

}
