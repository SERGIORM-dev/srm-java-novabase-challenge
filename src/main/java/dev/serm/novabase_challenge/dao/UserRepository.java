package dev.serm.novabase_challenge.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.serm.novabase_challenge.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
