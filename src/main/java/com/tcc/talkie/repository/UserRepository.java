package com.tcc.talkie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcc.talkie.domain.user.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByCpfHash(String cpfHash);
}