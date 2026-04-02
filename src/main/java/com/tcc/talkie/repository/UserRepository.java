package com.tcc.talkie.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcc.talkie.domain.user.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByCpfHash(String cpfHash);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);
}